package com.example.datamagic.service;

import com.example.datamagic.model.entity.FieldTransformationLog;
import com.example.datamagic.model.entity.ProcessingRecord;
import com.example.datamagic.model.entity.TransformationRule;
import com.example.datamagic.model.enums.ProcessingStatus;
import com.example.datamagic.repository.FieldTransformationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileProcessingService {

    private static final char DELIMITER = (char) 27;
    private static final String FILE_PATTERN = "_SJBX_";

    private final ProcessingRecordService processingRecordService;
    private final TransformationRuleService transformationRuleService;
    private final FieldTransformationLogRepository fieldTransformationLogRepository;
    private final FtpService ftpService;
    private final SystemConfigService systemConfigService;

    @Transactional
    public void processFile(String filePath) {
        File sourceFile = new File(filePath);
        if (!sourceFile.exists()) {
            log.error("Source file does not exist: {}", filePath);
            return;
        }

        ProcessingRecord record = ProcessingRecord.builder()
                .sourceFileName(sourceFile.getName())
                .sourceFilePath(filePath)
                .status(ProcessingStatus.processing)
                .startTime(LocalDateTime.now())
                .totalRecordCount(0)
                .transformedFieldCount(0)
                .build();
        record = processingRecordService.saveEntity(record);

        try {
            String targetFilePath = generateTargetFilePath(filePath);
            int totalRecords = 0;
            int transformedFields = 0;

            List<TransformationRule> activeRules = transformationRuleService.getActiveRuleEntities();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(sourceFile), StandardCharsets.UTF_8));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                         new FileOutputStream(targetFilePath), StandardCharsets.UTF_8))) {

                String line;
                int lineNumber = 0;
                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    totalRecords++;
                    String transformedLine = processLine(line, lineNumber, activeRules, record);
                    transformedFields += countTransformedFields(line, transformedLine);
                    writer.write(transformedLine);
                    writer.newLine();
                }
            }

            record.setTargetFilePath(targetFilePath);
            record.setTargetFileName(new File(targetFilePath).getName());
            record.setTotalRecordCount(totalRecords);
            record.setTransformedFieldCount(transformedFields);
            record.setStatus(ProcessingStatus.completed);
            record.setCompleteTime(LocalDateTime.now());

            boolean ftpUploaded = ftpService.uploadFile(targetFilePath);
            if (ftpUploaded) {
                record.setFtpUploaded(true);
                record.setFtpUploadTime(LocalDateTime.now());
                record.setStatus(ProcessingStatus.uploaded);
            }

            // Commented out: don't delete source file after processing
            // boolean sourceDeleted = deleteSourceFile(filePath);
            // if (sourceDeleted) {
            //     record.setSourceDeleted(true);
            //     record.setSourceDeleteTime(LocalDateTime.now());
            // }

            processingRecordService.saveEntity(record);
            log.info("File processed successfully: {} -> {}", filePath, targetFilePath);

        } catch (Exception e) {
            log.error("Error processing file: {}", filePath, e);
            record.setStatus(ProcessingStatus.failed);
            record.setErrorMessage(e.getMessage());
            record.setCompleteTime(LocalDateTime.now());
            processingRecordService.saveEntity(record);
        }
    }

    private String processLine(String line, int lineNumber, List<TransformationRule> rules, ProcessingRecord record) {
        String[] fields = line.split(String.valueOf(DELIMITER));
        StringBuilder result = new StringBuilder();
        int transformedCount = 0;

        for (int i = 0; i < fields.length; i++) {
            String field = fields[i];
            String transformedValue = field;

            for (TransformationRule rule : rules) {
                if (shouldTransform(field, rule.getRulePattern())) {
                    transformedValue = applyTransformation(field, rule);
                    saveTransformationLog(record, lineNumber, i, field, transformedValue, rule);
                    transformedCount++;
                    break;
                }
            }

            if (i > 0) {
                result.append(DELIMITER);
            }
            result.append(transformedValue);
        }

        return result.toString();
    }

    private boolean shouldTransform(String fieldValue, String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return false;
        }
        Pattern regexPattern = Pattern.compile(pattern);
        Matcher matcher = regexPattern.matcher(fieldValue);
        return matcher.find();
    }

    private String applyTransformation(String fieldValue, TransformationRule rule) {
        String transformationType = rule.getTransformationType();
        String params = rule.getTransformationParams();
        String pattern = rule.getRulePattern();

        String baseValue = removeSuffix(fieldValue, pattern);
        String transformedValue = baseValue;

        if ("remove_suffix".equals(transformationType)) {
            transformedValue = baseValue;
        } else if ("replace".equals(transformationType)) {
            transformedValue = replaceTransformation(baseValue, params);
        } else if ("hash".equals(transformationType)) {
            transformedValue = hashTransformation(baseValue);
        } else if ("mask".equals(transformationType)) {
            transformedValue = maskTransformation(baseValue, params);
        } else if ("encrypt".equals(transformationType)) {
            transformedValue = baseValue;
        }

        return transformedValue;
    }

    private String removeSuffix(String value, String suffix) {
        if (suffix == null || suffix.isEmpty() || !value.endsWith(suffix)) {
            return value;
        }
        return value.substring(0, value.length() - suffix.length());
    }

    private String replaceTransformation(String value, String params) {
        if (params == null || !params.contains(",")) {
            return value;
        }
        String[] parts = params.split(",");
        if (parts.length >= 2) {
            return value.replace(parts[0], parts[1]);
        }
        return value;
    }

    private String hashTransformation(String value) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return String.valueOf(value.hashCode());
        }
    }

    private String maskTransformation(String value, String params) {
        int showLast = 4;
        if (params != null && params.contains("showLast")) {
            try {
                int idx = params.indexOf("showLast");
                int start = params.indexOf(":", idx) + 1;
                int end = params.indexOf("}", start);
                showLast = Integer.parseInt(params.substring(start, end).trim());
            } catch (Exception e) {
                showLast = 4;
            }
        }
        if (value.length() <= showLast) {
            return "*".repeat(value.length());
        }
        return value.substring(0, showLast) + "*".repeat(value.length() - showLast);
    }

    private int countTransformedFields(String originalLine, String transformedLine) {
        String[] originalFields = originalLine.split(String.valueOf(DELIMITER));
        String[] transformedFields = transformedLine.split(String.valueOf(DELIMITER));

        int count = 0;
        for (int i = 0; i < originalFields.length && i < transformedFields.length; i++) {
            if (!originalFields[i].equals(transformedFields[i])) {
                count++;
            }
        }
        return count;
    }

    private void saveTransformationLog(ProcessingRecord record, int lineNumber, int fieldIndex,
                                         String originalValue, String transformedValue, TransformationRule rule) {
        FieldTransformationLog log = FieldTransformationLog.builder()
                .processingRecord(record)
                .lineNumber(lineNumber)
                .fieldIndex(fieldIndex)
                .originalValue(originalValue)
                .transformedValue(transformedValue)
                .ruleId(rule.getId())
                .ruleCode(rule.getRuleCode())
                .build();
        fieldTransformationLogRepository.save(log);
    }

    private String generateTargetFilePath(String sourceFilePath) {
        String scanDirectory = systemConfigService.getConfigValue("scan_directory");
        if (scanDirectory == null || scanDirectory.isEmpty()) {
            scanDirectory = new File(sourceFilePath).getParent();
        }

        File sourceFile = new File(sourceFilePath);
        String baseName = sourceFile.getName();
        baseName = baseName.replace("SJBX", "BXJG");

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String targetFileName = baseName + "_" + timestamp;

        return scanDirectory + File.separator + targetFileName;
    }

    private boolean deleteSourceFile(String filePath) {
        try {
            File file = new File(filePath);
            return file.delete();
        } catch (Exception e) {
            log.error("Error deleting source file: {}", filePath, e);
            return false;
        }
    }
}

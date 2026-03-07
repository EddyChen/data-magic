package com.example.datamagic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileScanService {

    private static final String FILE_PATTERN = "_SJBX_";

    private final FileProcessingService fileProcessingService;
    private final SystemConfigService systemConfigService;

    @Scheduled(cron = "${file.scan.cron:0 */5 * * * *}")
    public void scanAndProcessFiles() {
        String scanDirectory = systemConfigService.getConfigValue("scan_directory");
        if (scanDirectory == null || scanDirectory.isEmpty()) {
            log.warn("Scan directory not configured");
            return;
        }

        File directory = new File(scanDirectory);
        if (!directory.exists() || !directory.isDirectory()) {
            log.error("Invalid scan directory: {}", scanDirectory);
            return;
        }

        log.info("Scanning directory: {}", scanDirectory);
        File[] files = directory.listFiles((dir, name) -> name.contains(FILE_PATTERN));

        if (files == null || files.length == 0) {
            log.info("No files found matching pattern: {}", FILE_PATTERN);
            return;
        }

        log.info("Found {} files to process", files.length);
        Arrays.stream(files)
                .filter(File::isFile)
                .sorted((f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()))
                .forEach(file -> {
                    try {
                        log.info("Processing file: {}", file.getAbsolutePath());
                        fileProcessingService.processFile(file.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("Error processing file: {}", file.getName(), e);
                    }
                });
    }

    public void triggerScan() {
        scanAndProcessFiles();
    }
}

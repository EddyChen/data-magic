package com.example.datamagic.service;

import com.example.datamagic.model.dto.PageResult;
import com.example.datamagic.model.dto.ProcessingRecordDTO;
import com.example.datamagic.model.entity.ProcessingRecord;
import com.example.datamagic.model.enums.ProcessingStatus;
import com.example.datamagic.repository.ProcessingRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcessingRecordService {

    private static final List<ProcessingStatus> PROCESSED_STATUSES = Arrays.asList(
            ProcessingStatus.processing,
            ProcessingStatus.completed,
            ProcessingStatus.uploaded
    );

    private final ProcessingRecordRepository processingRecordRepository;

    public PageResult<ProcessingRecordDTO> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProcessingRecord> records = processingRecordRepository.findByOrderByCreatedAtDesc(pageable);
        return toPageResult(records);
    }

    public PageResult<ProcessingRecordDTO> findByStatus(ProcessingStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProcessingRecord> records = processingRecordRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        return toPageResult(records);
    }

    public ProcessingRecordDTO findById(Long id) {
        return processingRecordRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    @Transactional
    public ProcessingRecordDTO create(ProcessingRecordDTO dto) {
        ProcessingRecord record = ProcessingRecord.builder()
                .sourceFileName(dto.getSourceFileName())
                .sourceFilePath(dto.getSourceFilePath())
                .targetFileName(dto.getTargetFileName())
                .targetFilePath(dto.getTargetFilePath())
                .totalRecordCount(dto.getTotalRecordCount() != null ? dto.getTotalRecordCount() : 0)
                .transformedFieldCount(dto.getTransformedFieldCount() != null ? dto.getTransformedFieldCount() : 0)
                .status(dto.getStatus() != null ? dto.getStatus() : ProcessingStatus.pending)
                .errorMessage(dto.getErrorMessage())
                .startTime(dto.getStartTime() != null ? dto.getStartTime() : LocalDateTime.now())
                .completeTime(dto.getCompleteTime())
                .ftpUploaded(dto.getFtpUploaded() != null ? dto.getFtpUploaded() : false)
                .sourceDeleted(dto.getSourceDeleted() != null ? dto.getSourceDeleted() : false)
                .build();
        return toDTO(processingRecordRepository.save(record));
    }

    @Transactional
    public ProcessingRecordDTO update(Long id, ProcessingRecordDTO dto) {
        return processingRecordRepository.findById(id)
                .map(record -> {
                    if (dto.getTargetFileName() != null) record.setTargetFileName(dto.getTargetFileName());
                    if (dto.getTargetFilePath() != null) record.setTargetFilePath(dto.getTargetFilePath());
                    if (dto.getTotalRecordCount() != null) record.setTotalRecordCount(dto.getTotalRecordCount());
                    if (dto.getTransformedFieldCount() != null) record.setTransformedFieldCount(dto.getTransformedFieldCount());
                    if (dto.getStatus() != null) record.setStatus(dto.getStatus());
                    if (dto.getErrorMessage() != null) record.setErrorMessage(dto.getErrorMessage());
                    if (dto.getCompleteTime() != null) record.setCompleteTime(dto.getCompleteTime());
                    if (dto.getFtpUploaded() != null) record.setFtpUploaded(dto.getFtpUploaded());
                    if (dto.getFtpUploadTime() != null) record.setFtpUploadTime(dto.getFtpUploadTime());
                    if (dto.getSourceDeleted() != null) record.setSourceDeleted(dto.getSourceDeleted());
                    if (dto.getSourceDeleteTime() != null) record.setSourceDeleteTime(dto.getSourceDeleteTime());
                    return toDTO(processingRecordRepository.save(record));
                })
                .orElse(null);
    }

    @Transactional
    public void delete(Long id) {
        processingRecordRepository.deleteById(id);
    }

    public ProcessingRecord getEntityById(Long id) {
        return processingRecordRepository.findById(id).orElse(null);
    }

    public ProcessingRecord saveEntity(ProcessingRecord record) {
        return processingRecordRepository.save(record);
    }

    public boolean isFileProcessed(String sourceFilePath) {
        return processingRecordRepository.existsBySourceFilePathAndStatusIn(sourceFilePath, PROCESSED_STATUSES);
    }

    private PageResult<ProcessingRecordDTO> toPageResult(Page<ProcessingRecord> page) {
        List<ProcessingRecordDTO> content = page.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return PageResult.<ProcessingRecordDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    private ProcessingRecordDTO toDTO(ProcessingRecord record) {
        return ProcessingRecordDTO.builder()
                .id(record.getId())
                .sourceFileName(record.getSourceFileName())
                .sourceFilePath(record.getSourceFilePath())
                .targetFileName(record.getTargetFileName())
                .targetFilePath(record.getTargetFilePath())
                .totalRecordCount(record.getTotalRecordCount())
                .transformedFieldCount(record.getTransformedFieldCount())
                .status(record.getStatus())
                .errorMessage(record.getErrorMessage())
                .startTime(record.getStartTime())
                .completeTime(record.getCompleteTime())
                .ftpUploaded(record.getFtpUploaded())
                .ftpUploadTime(record.getFtpUploadTime())
                .sourceDeleted(record.getSourceDeleted())
                .sourceDeleteTime(record.getSourceDeleteTime())
                .createdAt(record.getCreatedAt())
                .build();
    }
}

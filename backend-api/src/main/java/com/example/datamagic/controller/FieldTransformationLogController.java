package com.example.datamagic.controller;

import com.example.datamagic.model.dto.FieldTransformationLogDTO;
import com.example.datamagic.model.dto.PageResult;
import com.example.datamagic.model.entity.FieldTransformationLog;
import com.example.datamagic.repository.FieldTransformationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transformation-logs")
@RequiredArgsConstructor
public class FieldTransformationLogController {

    private final FieldTransformationLogRepository fieldTransformationLogRepository;

    @GetMapping("/processing-record/{processingRecordId}")
    public ResponseEntity<PageResult<FieldTransformationLogDTO>> getByProcessingRecordId(
            @PathVariable Long processingRecordId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<FieldTransformationLog> logs = fieldTransformationLogRepository
                .findByProcessingRecordIdOrderByLineNumberAscFieldIndexAsc(
                        processingRecordId, PageRequest.of(page, size));
        
        PageResult<FieldTransformationLogDTO> result = PageResult.<FieldTransformationLogDTO>builder()
                .content(logs.getContent().stream().map(this::toDTO).collect(java.util.stream.Collectors.toList()))
                .page(logs.getNumber())
                .size(logs.getSize())
                .totalElements(logs.getTotalElements())
                .totalPages(logs.getTotalPages())
                .first(logs.isFirst())
                .last(logs.isLast())
                .build();
        
        return ResponseEntity.ok(result);
    }

    private FieldTransformationLogDTO toDTO(FieldTransformationLog log) {
        return FieldTransformationLogDTO.builder()
                .id(log.getId())
                .processingRecordId(log.getProcessingRecord().getId())
                .lineNumber(log.getLineNumber())
                .fieldIndex(log.getFieldIndex())
                .fieldName(log.getFieldName())
                .originalValue(log.getOriginalValue())
                .transformedValue(log.getTransformedValue())
                .ruleId(log.getRuleId())
                .ruleCode(log.getRuleCode())
                .createdAt(log.getCreatedAt())
                .build();
    }
}

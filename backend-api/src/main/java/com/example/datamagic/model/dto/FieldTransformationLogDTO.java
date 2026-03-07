package com.example.datamagic.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldTransformationLogDTO {
    private Long id;
    private Long processingRecordId;
    private Integer lineNumber;
    private Integer fieldIndex;
    private String fieldName;
    private String originalValue;
    private String transformedValue;
    private Long ruleId;
    private String ruleCode;
    private LocalDateTime createdAt;
}

package com.example.datamagic.model.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransformationRuleDTO {
    private Long id;

    @NotBlank(message = "Rule code is required")
    private String ruleCode;

    @NotBlank(message = "Rule pattern is required")
    private String rulePattern;

    @NotBlank(message = "Transformation type is required")
    private String transformationType;

    private String transformationParams;
    private Boolean isActive;
    private Integer priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

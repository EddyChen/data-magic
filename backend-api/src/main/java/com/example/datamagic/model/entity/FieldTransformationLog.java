package com.example.datamagic.model.entity;

import javax.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "field_transformation_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldTransformationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processing_record_id", nullable = false)
    private ProcessingRecord processingRecord;

    @Column(name = "line_number", nullable = false)
    private Integer lineNumber;

    @Column(name = "field_index", nullable = false)
    private Integer fieldIndex;

    @Column(name = "field_name", length = 100)
    private String fieldName;

    @Column(name = "original_value", length = 500)
    private String originalValue;

    @Column(name = "transformed_value", length = 500)
    private String transformedValue;

    @Column(name = "rule_id")
    private Long ruleId;

    @Column(name = "rule_code", length = 50)
    private String ruleCode;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

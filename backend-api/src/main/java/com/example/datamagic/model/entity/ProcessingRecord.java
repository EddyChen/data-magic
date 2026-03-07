package com.example.datamagic.model.entity;

import com.example.datamagic.model.enums.ProcessingStatus;
import javax.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "processing_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_file_name", nullable = false, length = 255)
    private String sourceFileName;

    @Column(name = "source_file_path", nullable = false, length = 500)
    private String sourceFilePath;

    @Column(name = "target_file_name", length = 255)
    private String targetFileName;

    @Column(name = "target_file_path", length = 500)
    private String targetFilePath;

    @Column(name = "total_record_count")
    @Builder.Default
    private Integer totalRecordCount = 0;

    @Column(name = "transformed_field_count")
    @Builder.Default
    private Integer transformedFieldCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private ProcessingStatus status = ProcessingStatus.pending;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "complete_time")
    private LocalDateTime completeTime;

    @Column(name = "ftp_uploaded")
    @Builder.Default
    private Boolean ftpUploaded = false;

    @Column(name = "ftp_upload_time")
    private LocalDateTime ftpUploadTime;

    @Column(name = "source_deleted")
    @Builder.Default
    private Boolean sourceDeleted = false;

    @Column(name = "source_delete_time")
    private LocalDateTime sourceDeleteTime;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

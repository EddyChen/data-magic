package com.example.datamagic.model.dto;

import com.example.datamagic.model.enums.ProcessingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingRecordDTO {
    private Long id;
    private String sourceFileName;
    private String sourceFilePath;
    private String targetFileName;
    private String targetFilePath;
    private Integer totalRecordCount;
    private Integer transformedFieldCount;
    private ProcessingStatus status;
    private String errorMessage;
    private LocalDateTime startTime;
    private LocalDateTime completeTime;
    private Boolean ftpUploaded;
    private LocalDateTime ftpUploadTime;
    private Boolean sourceDeleted;
    private LocalDateTime sourceDeleteTime;
    private LocalDateTime createdAt;
}

package com.example.datamagic.repository;

import com.example.datamagic.model.entity.ProcessingRecord;
import com.example.datamagic.model.enums.ProcessingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProcessingRecordRepository extends JpaRepository<ProcessingRecord, Long> {

    Page<ProcessingRecord> findByOrderByCreatedAtDesc(Pageable pageable);

    Page<ProcessingRecord> findByStatusOrderByCreatedAtDesc(ProcessingStatus status, Pageable pageable);

    List<ProcessingRecord> findByStatusIn(List<ProcessingStatus> statuses);

    List<ProcessingRecord> findBySourceFileNameContaining(String fileName);

    Page<ProcessingRecord> findByCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    List<ProcessingRecord> findBySourceFilePath(String sourceFilePath);

    boolean existsBySourceFilePathAndStatusIn(String sourceFilePath, List<ProcessingStatus> statuses);
}

package com.example.datamagic.repository;

import com.example.datamagic.model.entity.FieldTransformationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldTransformationLogRepository extends JpaRepository<FieldTransformationLog, Long> {

    List<FieldTransformationLog> findByProcessingRecordId(Long processingRecordId);

    Page<FieldTransformationLog> findByProcessingRecordIdOrderByLineNumberAscFieldIndexAsc(
            Long processingRecordId, Pageable pageable);

    Long countByProcessingRecordId(Long processingRecordId);
}

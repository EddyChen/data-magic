package com.example.datamagic.controller;

import com.example.datamagic.model.dto.PageResult;
import com.example.datamagic.model.dto.ProcessingRecordDTO;
import com.example.datamagic.model.enums.ProcessingStatus;
import com.example.datamagic.service.ProcessingRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/processing-records")
@RequiredArgsConstructor
public class ProcessingRecordController {

    private final ProcessingRecordService processingRecordService;

    @GetMapping
    public ResponseEntity<PageResult<ProcessingRecordDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(processingRecordService.findAll(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProcessingRecordDTO> getById(@PathVariable Long id) {
        ProcessingRecordDTO dto = processingRecordService.findById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<PageResult<ProcessingRecordDTO>> getByStatus(
            @PathVariable ProcessingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(processingRecordService.findByStatus(status, page, size));
    }
}

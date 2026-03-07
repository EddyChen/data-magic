package com.example.datamagic.controller;

import com.example.datamagic.model.dto.TransformationRuleDTO;
import com.example.datamagic.service.TransformationRuleService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transformation-rules")
@RequiredArgsConstructor
public class TransformationRuleController {

    private final TransformationRuleService transformationRuleService;

    @GetMapping
    public ResponseEntity<List<TransformationRuleDTO>> getAll() {
        return ResponseEntity.ok(transformationRuleService.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<TransformationRuleDTO>> getActiveRules() {
        return ResponseEntity.ok(transformationRuleService.findActiveRules());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransformationRuleDTO> getById(@PathVariable Long id) {
        TransformationRuleDTO dto = transformationRuleService.findById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<TransformationRuleDTO> create(@Valid @RequestBody TransformationRuleDTO dto) {
        return ResponseEntity.ok(transformationRuleService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransformationRuleDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody TransformationRuleDTO dto) {
        TransformationRuleDTO updated = transformationRuleService.update(id, dto);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transformationRuleService.delete(id);
        return ResponseEntity.ok().build();
    }
}

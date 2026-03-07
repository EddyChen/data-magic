package com.example.datamagic.controller;

import com.example.datamagic.model.dto.SystemConfigDTO;
import com.example.datamagic.service.SystemConfigService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system-configs")
@RequiredArgsConstructor
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    @GetMapping
    public ResponseEntity<List<SystemConfigDTO>> getAll() {
        return ResponseEntity.ok(systemConfigService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SystemConfigDTO> getById(@PathVariable Long id) {
        SystemConfigDTO dto = systemConfigService.findById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/key/{key}")
    public ResponseEntity<SystemConfigDTO> getByKey(@PathVariable String key) {
        SystemConfigDTO dto = systemConfigService.findByConfigKey(key);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<SystemConfigDTO> create(@Valid @RequestBody SystemConfigDTO dto) {
        return ResponseEntity.ok(systemConfigService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SystemConfigDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody SystemConfigDTO dto) {
        SystemConfigDTO updated = systemConfigService.update(id, dto);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        systemConfigService.delete(id);
        return ResponseEntity.ok().build();
    }
}

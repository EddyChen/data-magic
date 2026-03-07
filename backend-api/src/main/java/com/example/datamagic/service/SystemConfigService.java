package com.example.datamagic.service;

import com.example.datamagic.model.dto.SystemConfigDTO;
import com.example.datamagic.model.entity.SystemConfig;
import com.example.datamagic.repository.SystemConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemConfigService {

    private final SystemConfigRepository systemConfigRepository;

    public List<SystemConfigDTO> findAll() {
        return systemConfigRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public SystemConfigDTO findById(Long id) {
        return systemConfigRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public SystemConfigDTO findByConfigKey(String key) {
        return systemConfigRepository.findByConfigKey(key)
                .map(this::toDTO)
                .orElse(null);
    }

    public String getConfigValue(String key) {
        return systemConfigRepository.findByConfigKey(key)
                .map(SystemConfig::getConfigValue)
                .orElse(null);
    }

    @Transactional
    public SystemConfigDTO create(SystemConfigDTO dto) {
        SystemConfig config = SystemConfig.builder()
                .configKey(dto.getConfigKey())
                .configValue(dto.getConfigValue())
                .description(dto.getDescription())
                .build();
        return toDTO(systemConfigRepository.save(config));
    }

    @Transactional
    public SystemConfigDTO update(Long id, SystemConfigDTO dto) {
        return systemConfigRepository.findById(id)
                .map(config -> {
                    if (dto.getConfigValue() != null) config.setConfigValue(dto.getConfigValue());
                    if (dto.getDescription() != null) config.setDescription(dto.getDescription());
                    return toDTO(systemConfigRepository.save(config));
                })
                .orElse(null);
    }

    @Transactional
    public void delete(Long id) {
        systemConfigRepository.deleteById(id);
    }

    public SystemConfig getEntityById(Long id) {
        return systemConfigRepository.findById(id).orElse(null);
    }

    public SystemConfig saveEntity(SystemConfig config) {
        return systemConfigRepository.save(config);
    }

    private SystemConfigDTO toDTO(SystemConfig config) {
        return SystemConfigDTO.builder()
                .id(config.getId())
                .configKey(config.getConfigKey())
                .configValue(config.getConfigValue())
                .description(config.getDescription())
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .build();
    }
}

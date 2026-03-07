package com.example.datamagic.service;

import com.example.datamagic.model.dto.TransformationRuleDTO;
import com.example.datamagic.model.entity.TransformationRule;
import com.example.datamagic.repository.TransformationRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransformationRuleService {

    private final TransformationRuleRepository transformationRuleRepository;

    public List<TransformationRuleDTO> findAll() {
        return transformationRuleRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<TransformationRuleDTO> findActiveRules() {
        return transformationRuleRepository.findByIsActiveTrueOrderByPriorityDesc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public TransformationRuleDTO findById(Long id) {
        return transformationRuleRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public TransformationRuleDTO findByRuleCode(String ruleCode) {
        return transformationRuleRepository.findByRuleCode(ruleCode)
                .map(this::toDTO)
                .orElse(null);
    }

    @Transactional
    public TransformationRuleDTO create(TransformationRuleDTO dto) {
        TransformationRule rule = TransformationRule.builder()
                .ruleCode(dto.getRuleCode())
                .rulePattern(dto.getRulePattern())
                .transformationType(dto.getTransformationType())
                .transformationParams(dto.getTransformationParams())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .priority(dto.getPriority() != null ? dto.getPriority() : 0)
                .build();
        return toDTO(transformationRuleRepository.save(rule));
    }

    @Transactional
    public TransformationRuleDTO update(Long id, TransformationRuleDTO dto) {
        return transformationRuleRepository.findById(id)
                .map(rule -> {
                    if (dto.getRuleCode() != null) rule.setRuleCode(dto.getRuleCode());
                    if (dto.getRulePattern() != null) rule.setRulePattern(dto.getRulePattern());
                    if (dto.getTransformationType() != null) rule.setTransformationType(dto.getTransformationType());
                    if (dto.getTransformationParams() != null) rule.setTransformationParams(dto.getTransformationParams());
                    if (dto.getIsActive() != null) rule.setIsActive(dto.getIsActive());
                    if (dto.getPriority() != null) rule.setPriority(dto.getPriority());
                    return toDTO(transformationRuleRepository.save(rule));
                })
                .orElse(null);
    }

    @Transactional
    public void delete(Long id) {
        transformationRuleRepository.deleteById(id);
    }

    public List<TransformationRule> getActiveRuleEntities() {
        return transformationRuleRepository.findByIsActiveTrueOrderByPriorityDesc();
    }

    public TransformationRule getEntityById(Long id) {
        return transformationRuleRepository.findById(id).orElse(null);
    }

    public TransformationRule saveEntity(TransformationRule rule) {
        return transformationRuleRepository.save(rule);
    }

    private TransformationRuleDTO toDTO(TransformationRule rule) {
        return TransformationRuleDTO.builder()
                .id(rule.getId())
                .ruleCode(rule.getRuleCode())
                .rulePattern(rule.getRulePattern())
                .transformationType(rule.getTransformationType())
                .transformationParams(rule.getTransformationParams())
                .isActive(rule.getIsActive())
                .priority(rule.getPriority())
                .createdAt(rule.getCreatedAt())
                .updatedAt(rule.getUpdatedAt())
                .build();
    }
}

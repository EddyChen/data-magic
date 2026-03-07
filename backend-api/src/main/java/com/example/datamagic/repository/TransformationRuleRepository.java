package com.example.datamagic.repository;

import com.example.datamagic.model.entity.TransformationRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransformationRuleRepository extends JpaRepository<TransformationRule, Long> {

    Optional<TransformationRule> findByRuleCode(String ruleCode);

    List<TransformationRule> findByIsActiveTrueOrderByPriorityDesc();

    List<TransformationRule> findByRulePatternContaining(String pattern);
}

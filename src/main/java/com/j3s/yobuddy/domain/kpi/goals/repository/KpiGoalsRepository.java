package com.j3s.yobuddy.domain.kpi.goals.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.j3s.yobuddy.domain.kpi.goals.entity.KpiGoals;

@Repository
public interface KpiGoalsRepository extends JpaRepository<KpiGoals, Long> {

    List<KpiGoals> findAllByIsDeletedFalse();

    Optional<KpiGoals> findByKpiGoalIdAndIsDeletedFalse(Long kpiGoalId);

    List<KpiGoals> findByDepartmentIdAndIsDeletedFalse(Long departmentId);

    List<KpiGoals> findByDescriptionContainingIgnoreCaseAndIsDeletedFalse(String description);

    Optional<KpiGoals> findByDepartmentIdAndKpiCategoryIdAndIsDeletedFalse(
        Long departmentId,
        Long kpiCategoryId
    );
}

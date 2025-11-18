package com.j3s.yobuddy.domain.kpi.results.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.j3s.yobuddy.domain.kpi.results.entity.KpiResults;

@Repository
public interface KpiResultsRepository extends JpaRepository<KpiResults, Long> {

    List<KpiResults> findAllByIsDeletedFalse();

    Optional<KpiResults> findByKpiResultIdAndIsDeletedFalse(Long kpiResultId);

    List<KpiResults> findByKpiGoalIdAndIsDeletedFalse(Long kpiGoalId);

    List<KpiResults> findByUserIdAndIsDeletedFalse(Long userId);

    List<KpiResults> findByDepartmentIdAndIsDeletedFalse(Long departmentId);
}

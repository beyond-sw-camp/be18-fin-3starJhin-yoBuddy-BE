package com.j3s.yobuddy.domain.kpi.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.j3s.yobuddy.domain.kpi.category.entity.KpiCategory;

@Repository
public interface KpiCategoryRepository extends JpaRepository<KpiCategory, Long> {

    List<KpiCategory> findAllByIsDeletedFalse();

    Optional<KpiCategory> findByKpiCategoryIdAndIsDeletedFalse(Long kpiCategoryId);

    List<KpiCategory> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name);
}

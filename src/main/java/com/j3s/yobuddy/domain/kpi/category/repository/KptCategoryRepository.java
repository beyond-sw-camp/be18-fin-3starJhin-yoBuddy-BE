package com.j3s.yobuddy.domain.kpi.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.j3s.yobuddy.domain.kpi.category.entity.KptCategory;

@Repository
public interface KptCategoryRepository extends JpaRepository<KptCategory, Long> {

    List<KptCategory> findAllByIsDeletedFalse();

    Optional<KptCategory> findByKpiCategoryIdAndIsDeletedFalse(Long kpiCategoryId);

    List<KptCategory> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name);
}

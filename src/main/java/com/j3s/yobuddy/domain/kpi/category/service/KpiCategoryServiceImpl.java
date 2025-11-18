package com.j3s.yobuddy.domain.kpi.category.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.j3s.yobuddy.domain.kpi.category.dto.request.KpiCategoryRequest;
import com.j3s.yobuddy.domain.kpi.category.dto.response.KpiCategoryListResponse;
import com.j3s.yobuddy.domain.kpi.category.dto.response.KpiCategoryResponse;
import com.j3s.yobuddy.domain.kpi.category.entity.KpiCategory;
import com.j3s.yobuddy.domain.kpi.category.exception.KpiCategoryAlreadyDeletedException;
import com.j3s.yobuddy.domain.kpi.category.exception.KpiCategoryNotFoundException;
import com.j3s.yobuddy.domain.kpi.category.repository.KpiCategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KpiCategoryServiceImpl implements KpiCategoryService {

    private final KpiCategoryRepository kpiCategoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<KpiCategoryListResponse> getCategories(String name) {
        List<KpiCategory> result = (name == null || name.isBlank())
            ? kpiCategoryRepository.findAllByIsDeletedFalse()
            : kpiCategoryRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(name);

        return result.stream().map(KpiCategoryListResponse::from).toList();
    }

    @Override
    @Transactional
    public void createCategory(KpiCategoryRequest request) {
        KpiCategory category = KpiCategory.builder()
            .name(request.getName())
            .description(request.getDescription())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .isDeleted(false)
            .build();

        kpiCategoryRepository.save(category);
    }

    @Override
    @Transactional
    public KpiCategoryListResponse updateCategory(Long kpiCategoryId, KpiCategoryRequest request) {
        KpiCategory category = kpiCategoryRepository.findByKpiCategoryIdAndIsDeletedFalse(kpiCategoryId)
            .orElseThrow(() -> new KpiCategoryNotFoundException(kpiCategoryId));

        category.update(request.getName(), request.getDescription());

        kpiCategoryRepository.save(category);

        return KpiCategoryListResponse.builder()
            .kpiCategoryId(category.getKpiCategoryId())
            .name(category.getName())
            .createdAt(category.getCreatedAt())
            .updatedAt(category.getUpdatedAt())
            .build();
    }

    @Override
    @Transactional
    public void deleteCategory(Long kpiCategoryId) {
        KpiCategory category = kpiCategoryRepository.findByKpiCategoryIdAndIsDeletedFalse(kpiCategoryId)
            .orElseThrow(() -> new KpiCategoryNotFoundException(kpiCategoryId));

        if (category.getIsDeleted()) {
            throw new KpiCategoryAlreadyDeletedException(kpiCategoryId);
        }

        category.softDelete();
        kpiCategoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public KpiCategoryResponse getCategoryById(Long kpiCategoryId) {
        KpiCategory category = kpiCategoryRepository.findById(kpiCategoryId)
            .orElseThrow(() -> new KpiCategoryNotFoundException(kpiCategoryId));

        return KpiCategoryResponse.from(category);
    }
}

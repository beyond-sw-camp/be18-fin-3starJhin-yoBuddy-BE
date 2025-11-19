package com.j3s.yobuddy.domain.kpi.category.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.j3s.yobuddy.domain.kpi.category.dto.request.KptCategoryRequest;
import com.j3s.yobuddy.domain.kpi.category.dto.response.KptCategoryListResponse;
import com.j3s.yobuddy.domain.kpi.category.dto.response.KptCategoryResponse;
import com.j3s.yobuddy.domain.kpi.category.entity.KptCategory;
import com.j3s.yobuddy.domain.kpi.category.exception.KptCategoryAlreadyDeletedException;
import com.j3s.yobuddy.domain.kpi.category.exception.KptCategoryNotFoundException;
import com.j3s.yobuddy.domain.kpi.category.repository.KptCategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KptCategoryServiceImpl implements KptCategoryService {

    private final KptCategoryRepository kptCategoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<KptCategoryListResponse> getCategories(String name) {
        List<KptCategory> result = (name == null || name.isBlank())
            ? kptCategoryRepository.findAllByIsDeletedFalse()
            : kptCategoryRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(name);

        return result.stream().map(KptCategoryListResponse::from).toList();
    }

    @Override
    @Transactional
    public void createCategory(KptCategoryRequest request) {
        KptCategory category = KptCategory.builder()
            .name(request.getName())
            .description(request.getDescription())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .isDeleted(false)
            .build();

        kptCategoryRepository.save(category);
    }

    @Override
    @Transactional
    public KptCategoryListResponse updateCategory(Long kpiCategoryId, KptCategoryRequest request) {
        KptCategory category = kptCategoryRepository.findByKpiCategoryIdAndIsDeletedFalse(kpiCategoryId)
            .orElseThrow(() -> new KptCategoryNotFoundException(kpiCategoryId));

        category.update(request.getName(), request.getDescription());

        kptCategoryRepository.save(category);

        return KptCategoryListResponse.builder()
            .kpiCategoryId(category.getKpiCategoryId())
            .name(category.getName())
            .createdAt(category.getCreatedAt())
            .updatedAt(category.getUpdatedAt())
            .build();
    }

    @Override
    @Transactional
    public void deleteCategory(Long kpiCategoryId) {
        KptCategory category = kptCategoryRepository.findByKpiCategoryIdAndIsDeletedFalse(kpiCategoryId)
            .orElseThrow(() -> new KptCategoryNotFoundException(kpiCategoryId));

        if (category.getIsDeleted()) {
            throw new KptCategoryAlreadyDeletedException(kpiCategoryId);
        }

        category.softDelete();
        kptCategoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public KptCategoryResponse getCategoryById(Long kpiCategoryId) {
        KptCategory category = kptCategoryRepository.findById(kpiCategoryId)
            .orElseThrow(() -> new KptCategoryNotFoundException(kpiCategoryId));

        return KptCategoryResponse.from(category);
    }
}

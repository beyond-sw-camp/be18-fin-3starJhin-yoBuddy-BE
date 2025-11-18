package com.j3s.yobuddy.domain.kpi.category.service;

import java.util.List;

import com.j3s.yobuddy.domain.kpi.category.dto.request.KpiCategoryRequest;
import com.j3s.yobuddy.domain.kpi.category.dto.response.KpiCategoryListResponse;
import com.j3s.yobuddy.domain.kpi.category.dto.response.KpiCategoryResponse;

public interface KpiCategoryService {

    List<KpiCategoryListResponse> getCategories(String name);

    void createCategory(KpiCategoryRequest request);

    KpiCategoryListResponse updateCategory(Long kpiCategoryId, KpiCategoryRequest request);

    void deleteCategory(Long kpiCategoryId);

    KpiCategoryResponse getCategoryById(Long kpiCategoryId);
}

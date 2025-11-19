package com.j3s.yobuddy.domain.kpi.category.service;

import java.util.List;

import com.j3s.yobuddy.domain.kpi.category.dto.request.KptCategoryRequest;
import com.j3s.yobuddy.domain.kpi.category.dto.response.KptCategoryListResponse;
import com.j3s.yobuddy.domain.kpi.category.dto.response.KptCategoryResponse;

public interface KptCategoryService {

    List<KptCategoryListResponse> getCategories(String name);

    void createCategory(KptCategoryRequest request);

    KptCategoryListResponse updateCategory(Long kpiCategoryId, KptCategoryRequest request);

    void deleteCategory(Long kpiCategoryId);

    KptCategoryResponse getCategoryById(Long kpiCategoryId);
}

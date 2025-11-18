package com.j3s.yobuddy.api.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.j3s.yobuddy.domain.kpi.category.dto.request.KpiCategoryRequest;
import com.j3s.yobuddy.domain.kpi.category.dto.response.KpiCategoryListResponse;
import com.j3s.yobuddy.domain.kpi.category.dto.response.KpiCategoryResponse;
import com.j3s.yobuddy.domain.kpi.category.service.KpiCategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin/kpi-category")
public class AdminKptCategoryController {

    private final KpiCategoryService kptCategoryService;

    @GetMapping
    public ResponseEntity<List<KpiCategoryListResponse>> getCategories(
        @RequestParam(required = false) String name) {

        List<KpiCategoryListResponse> categories = kptCategoryService.getCategories(name);

        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{kpiCategoryId}")
    public ResponseEntity<KpiCategoryResponse> getCategoryById(
        @PathVariable("kpiCategoryId") Long kpiCategoryId) {
        KpiCategoryResponse response = kptCategoryService.getCategoryById(kpiCategoryId);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<String> createCategory(@RequestBody KpiCategoryRequest request) {
        kptCategoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body("KPI 카테고리가 성공적으로 생성되었습니다.");
    }

    @PatchMapping("/{kpiCategoryId}")
    public ResponseEntity<String> updateCategory(@PathVariable("kpiCategoryId") Long kpiCategoryId,
        @RequestBody KpiCategoryRequest request) {

        kptCategoryService.updateCategory(kpiCategoryId, request);
        return ResponseEntity.ok("KPI 카테고리가 성공적으로 수정되었습니다.");
    }

    @DeleteMapping("/{kpiCategoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable("kpiCategoryId") Long kpiCategoryId) {
        kptCategoryService.deleteCategory(kpiCategoryId);
        return ResponseEntity.ok("KPI 카테고리가 성공적으로 삭제되었습니다.");
    }
}

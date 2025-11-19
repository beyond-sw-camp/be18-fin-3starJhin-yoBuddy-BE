package com.j3s.yobuddy.domain.kpi.category.dto.response;

import java.time.LocalDateTime;

import com.j3s.yobuddy.domain.kpi.category.entity.KptCategory;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KptCategoryListResponse {

    private Long kpiCategoryId;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static KptCategoryListResponse from(KptCategory category) {
        return KptCategoryListResponse.builder()
            .kpiCategoryId(category.getKpiCategoryId())
            .name(category.getName())
            .createdAt(category.getCreatedAt())
            .updatedAt(category.getUpdatedAt())
            .build();
    }
}

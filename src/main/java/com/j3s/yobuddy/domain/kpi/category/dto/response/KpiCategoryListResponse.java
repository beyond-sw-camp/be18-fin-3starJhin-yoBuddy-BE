package com.j3s.yobuddy.domain.kpi.category.dto.response;

import java.time.LocalDateTime;

import com.j3s.yobuddy.domain.kpi.category.entity.KpiCategory;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KpiCategoryListResponse {

    private Long kpiCategoryId;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static KpiCategoryListResponse from(KpiCategory category) {
        return KpiCategoryListResponse.builder()
            .kpiCategoryId(category.getKpiCategoryId())
            .name(category.getName())
            .createdAt(category.getCreatedAt())
            .updatedAt(category.getUpdatedAt())
            .build();
    }
}

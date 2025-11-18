package com.j3s.yobuddy.domain.kpi.category.dto.response;

import java.time.LocalDateTime;

import com.j3s.yobuddy.domain.kpi.category.entity.KpiCategory;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KpiCategoryResponse {

    private Long kpiCategoryId;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static KpiCategoryResponse from(KpiCategory category) {
        return KpiCategoryResponse.builder()
            .kpiCategoryId(category.getKpiCategoryId())
            .name(category.getName())
            .description(category.getDescription())
            .createdAt(category.getCreatedAt())
            .updatedAt(category.getUpdatedAt())
            .build();
    }
}

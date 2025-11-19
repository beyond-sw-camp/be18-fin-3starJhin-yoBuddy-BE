package com.j3s.yobuddy.domain.kpi.category.dto.response;

import java.time.LocalDateTime;

import com.j3s.yobuddy.domain.kpi.category.entity.KpiCategory;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KpiCategoryListResponse {

    private final Long kpiCategoryId;
    private final String name;
    private final String description;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static KpiCategoryListResponse from(KpiCategory category) {
        return KpiCategoryListResponse.builder()
            .kpiCategoryId(category.getKpiCategoryId())
            .name(category.getName())
            .description(category.getDescription())
            .createdAt(category.getCreatedAt())
            .updatedAt(category.getUpdatedAt())
            .build();
    }
}

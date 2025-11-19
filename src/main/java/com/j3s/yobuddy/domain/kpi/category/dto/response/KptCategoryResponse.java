package com.j3s.yobuddy.domain.kpi.category.dto.response;

import java.time.LocalDateTime;

import com.j3s.yobuddy.domain.kpi.category.entity.KptCategory;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KptCategoryResponse {

    private Long kpiCategoryId;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static KptCategoryResponse from(KptCategory category) {
        return KptCategoryResponse.builder()
            .kpiCategoryId(category.getKpiCategoryId())
            .name(category.getName())
            .description(category.getDescription())
            .createdAt(category.getCreatedAt())
            .updatedAt(category.getUpdatedAt())
            .build();
    }
}

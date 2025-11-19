package com.j3s.yobuddy.domain.kpi.category.dto.response;

import java.time.LocalDateTime;

import com.j3s.yobuddy.domain.kpi.category.entity.KptCategory;

import lombok.Builder;
import lombok.Getter;

@Getter
public class KptCategoryListResponse {
    private final Long kpiCategoryId;
    private final String name;
    private final String description;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Builder
    public KptCategoryListResponse(Long kpiCategoryId, String name, String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.kpiCategoryId = kpiCategoryId;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static KptCategoryListResponse from(KptCategory c) {
        return KptCategoryListResponse.builder()
            .kpiCategoryId(c.getKpiCategoryId())
            .name(c.getName())
            .description(c.getDescription())
            .createdAt(c.getCreatedAt())
            .updatedAt(c.getUpdatedAt())
            .build();
    }

    public static KptCategoryListResponse.KptCategoryListResponseBuilder builder() {
        return new KptCategoryListResponse.KptCategoryListResponseBuilder();
    }

    // Builder class (manual, to avoid Lombok in generated code)
    public static class KptCategoryListResponseBuilder {
        private Long kpiCategoryId;
        private String name;
        private String description;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public KptCategoryListResponseBuilder kpiCategoryId(Long kpiCategoryId) {
            this.kpiCategoryId = kpiCategoryId;
            return this;
        }

        public KptCategoryListResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public KptCategoryListResponseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public KptCategoryListResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public KptCategoryListResponseBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public KptCategoryListResponse build() {
            return new KptCategoryListResponse(kpiCategoryId, name, description, createdAt, updatedAt);
        }
    }
}

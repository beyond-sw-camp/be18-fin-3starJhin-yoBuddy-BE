package com.j3s.yobuddy.domain.kpi.category.dto.response;

import java.time.LocalDateTime;

import com.j3s.yobuddy.domain.kpi.category.entity.KptCategory;

import lombok.Builder;
import lombok.Getter;

@Getter
public class KptCategoryResponse {
    private final Long kpiCategoryId;
    private final String name;
    private final String description;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Builder
    public KptCategoryResponse(Long kpiCategoryId, String name, String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.kpiCategoryId = kpiCategoryId;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static KptCategoryResponse from(KptCategory c) {
        return KptCategoryResponse.builder()
                .kpiCategoryId(c.getKpiCategoryId())
                .name(c.getName())
                .description(c.getDescription())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    public static KptCategoryResponse.KptCategoryResponseBuilder builder() {
        return new KptCategoryResponse.KptCategoryResponseBuilder();
    }

    public static class KptCategoryResponseBuilder {
        private Long kpiCategoryId;
        private String name;
        private String description;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public KptCategoryResponseBuilder kpiCategoryId(Long kpiCategoryId) {
            this.kpiCategoryId = kpiCategoryId;
            return this;
        }

        public KptCategoryResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public KptCategoryResponseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public KptCategoryResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public KptCategoryResponseBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public KptCategoryResponse build() {
            return new KptCategoryResponse(kpiCategoryId, name, description, createdAt, updatedAt);
        }
    }
}

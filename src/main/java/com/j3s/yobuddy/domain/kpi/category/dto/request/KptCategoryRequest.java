package com.j3s.yobuddy.domain.kpi.category.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class KptCategoryRequest {
    private final String name;
    private final String description;

    @Builder
    public KptCategoryRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }
}

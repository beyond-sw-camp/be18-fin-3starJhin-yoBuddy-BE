package com.j3s.yobuddy.domain.kpi.category.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KpiCategoryRequest {
    private final String name;
    private final String description;
}

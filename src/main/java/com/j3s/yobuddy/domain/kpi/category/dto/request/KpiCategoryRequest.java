package com.j3s.yobuddy.domain.kpi.category.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KpiCategoryRequest {
    private String name;
    private String description;
    private String fieldName;
    private String tableName;
}

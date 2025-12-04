package com.j3s.yobuddy.domain.kpi.category.exception;

public class KpiCategoryNotFoundException extends RuntimeException {

    public KpiCategoryNotFoundException(Long id) {
        super("KpiCategory not found: " + id);
    }
}

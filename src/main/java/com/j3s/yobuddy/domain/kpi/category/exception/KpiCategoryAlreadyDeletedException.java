package com.j3s.yobuddy.domain.kpi.category.exception;

public class KpiCategoryAlreadyDeletedException extends RuntimeException {

    public KpiCategoryAlreadyDeletedException(Long id) {
        super("kpiCategory already deleted: " + id);
    }
}

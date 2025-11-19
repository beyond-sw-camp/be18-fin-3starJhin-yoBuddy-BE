package com.j3s.yobuddy.domain.kpi.category.exception;

public class KptCategoryNotFoundException extends RuntimeException {

    public KptCategoryNotFoundException(Long id) {
        super("KptCategory not found: " + id);
    }
}

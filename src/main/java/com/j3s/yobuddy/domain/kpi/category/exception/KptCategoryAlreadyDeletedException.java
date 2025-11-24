package com.j3s.yobuddy.domain.kpi.category.exception;

public class KptCategoryAlreadyDeletedException extends RuntimeException {

    public KptCategoryAlreadyDeletedException(Long id) {
        super("KptCategory already deleted: " + id);
    }
}

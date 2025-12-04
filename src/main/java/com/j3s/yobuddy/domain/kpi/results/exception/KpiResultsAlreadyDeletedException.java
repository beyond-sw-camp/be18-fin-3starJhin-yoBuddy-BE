package com.j3s.yobuddy.domain.kpi.results.exception;

public class KpiResultsAlreadyDeletedException extends RuntimeException {

    public KpiResultsAlreadyDeletedException(Long id) {
        super("KpiResults already deleted: " + id);
    }
}

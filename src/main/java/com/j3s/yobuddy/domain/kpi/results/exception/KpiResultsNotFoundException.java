package com.j3s.yobuddy.domain.kpi.results.exception;

public class KpiResultsNotFoundException extends RuntimeException {

    public KpiResultsNotFoundException(Long id) {
        super("KpiResults not found: " + id);
    }
}

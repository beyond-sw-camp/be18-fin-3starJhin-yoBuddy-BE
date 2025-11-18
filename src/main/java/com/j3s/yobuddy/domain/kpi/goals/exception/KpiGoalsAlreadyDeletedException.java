package com.j3s.yobuddy.domain.kpi.goals.exception;

public class KpiGoalsAlreadyDeletedException extends RuntimeException {

    public KpiGoalsAlreadyDeletedException(Long id) {
        super("KpiGoals already deleted: " + id);
    }
}

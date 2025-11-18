package com.j3s.yobuddy.domain.kpi.goals.exception;

public class KpiGoalsNotFoundException extends RuntimeException {

    public KpiGoalsNotFoundException(Long id) {
        super("KpiGoals not found: " + id);
    }
}

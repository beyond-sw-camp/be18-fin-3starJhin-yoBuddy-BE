package com.j3s.yobuddy.domain.kpi.results.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class KpiAggregatedResult {

    private final Map<Long, Map<Long, BigDecimal>> data = new HashMap<>();

    public void put(Long userId, Long categoryId, BigDecimal value) {
        if (userId == null || categoryId == null) return;

        data.computeIfAbsent(userId, k -> new HashMap<>())
            .put(categoryId, value);
    }

    public BigDecimal get(Long userId, Long categoryId) {
        return data.getOrDefault(userId, Map.of())
            .getOrDefault(categoryId, BigDecimal.ZERO);
    }
}
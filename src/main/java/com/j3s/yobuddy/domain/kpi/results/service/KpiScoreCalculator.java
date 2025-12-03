package com.j3s.yobuddy.domain.kpi.results.service;

import java.math.BigDecimal;

import com.j3s.yobuddy.domain.kpi.results.dto.request.KpiResultsRequest;

/**
 * 인터페이스: KPI 결과의 score를 계산하는 전략을 정의합니다.
 * 필요시 다른 구현체로 교체할 수 있습니다.
 */
public interface KpiScoreCalculator {

    /**
     * 주어진 요청 정보와 관련 KPI 목표 목록으로부터 계산된 score를 반환합니다.
     * 반환값이 null이면 score를 설정하지 않습니다.
     *
     * @param request 입력 요청
     * @param relatedGoals departmentId로 조회한 KPI 목표 목록(없을 수 있음)
     * @return 계산된 score 또는 null
     */
    BigDecimal computeScore(KpiResultsRequest request);
}

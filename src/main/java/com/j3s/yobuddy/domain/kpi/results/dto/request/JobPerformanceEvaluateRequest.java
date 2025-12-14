package com.j3s.yobuddy.domain.kpi.results.dto.request;

import lombok.Getter;

@Getter
public class JobPerformanceEvaluateRequest {

    private Long userId;
    private Long departmentId;
    private Long kpiGoalId;

    private int understanding;     // 업무 이해도
    private int problemSolving;    // 문제 해결
    private int communication;     // 소통
    private int responsibility;   // 책임감
    private int growth;            // 성장 가능성

    public int totalScore() {
        return understanding
            + problemSolving
            + communication
            + responsibility
            + growth;
    }
}
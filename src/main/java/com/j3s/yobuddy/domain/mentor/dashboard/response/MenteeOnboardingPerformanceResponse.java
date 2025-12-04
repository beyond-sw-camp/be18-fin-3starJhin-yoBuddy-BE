package com.j3s.yobuddy.domain.mentor.dashboard.response;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MenteeOnboardingPerformanceResponse {

    private Header header;                  // 상단 요약 영역
    private TaskSection taskSection;        // "과제 제출 현황" 카드
    private TrainingSection trainingSection; // "교육 이수 현황" 카드
    private List<WeeklyReportItem> weeklyReports; // 하단 "주간 리포트" 테이블

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Header {
        private Long menteeId;
        private String menteeName;
        private LocalDate periodStart;
        private LocalDate periodEnd;

        private int mentoringCount;       // 진행한 멘토링 횟수
        private double totalMentoringHours; // 총 멘토링 시간 (시간 단위)
        private Double averageTaskScore;    // 평균 과제 점수 (null 가능)
        private Double kpiScore;           // KPI 점수 (null 가능)
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TaskSection {
        private int totalTasks;           // 전체 과제 수
        private int submittedTasks;       // 제출 완료
        private int remainingTasks;       // 남은 과제
        private double submissionRate;    // 제출 기한 준수율 (%)
        private double onTimeRate;        // 제시간 제출 비율 (%)
        private double lateRate;          // 지각 제출 비율 (%)
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TrainingSection {
        private int totalTrainings;       // 전체 교육 수
        private int completedTrainings;   // 이수 완료
        private int remainingTrainings;   // 남은 교육
        private double completionRate;    // 이수 기한 준수율 (%)
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class WeeklyReportItem {
        private Long reportId;
        private String authorName;        // 작성자 (작성자)
        private String label;             // "1주차", "2주차" ...
        private LocalDate writtenDate;    // 작성일
        private String submitStatus;      // "SUBMITTED", "MISSING" ...
        private String feedbackStatus;    // "WRITTEN", "PENDING" ...
    }
}
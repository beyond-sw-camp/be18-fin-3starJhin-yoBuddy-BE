package com.j3s.yobuddy.domain.kpi.results.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserKpiDetailResponse {

    private UserInfo user;
    private ProgramInfo program;

    private long mentoringCount;
    private BigDecimal avgTaskScore;
    private BigDecimal totalKpiScore;

    // 레이더: goal 기준(= 사진의 기술역량/팀협업 같은 축이 goal.description이라 가정)
    private List<RadarItem> userRadar;
    private List<RadarItem> deptRadar;

    private ProgressSummary task;
    private ProgressSummary education;

    private List<WeeklyCard> weeklyReports;

    @Getter @Builder
    public static class UserInfo {
        private Long userId;
        private String name;
        private String departmentName;
        private String profileImageUrl;
    }

    @Getter @Builder
    public static class ProgramInfo {
        private Long programId;
        private String name;
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Getter @Builder
    public static class RadarItem {
        private Long kpiGoalId;
        private String label;      // goal.description
        private BigDecimal score;  // 개인(최신 1개) / 부서평균(avg)
    }

    @Getter @Builder
    public static class ProgressSummary {
        private int completedCount;
        private int remainingCount;

        private int onTimeCount;
        private int lateCount;

        private int progressPercent; // completed/(completed+remaining) * 100
    }

    @Getter @Builder
    public static class WeeklyCard {
        private Long weeklyReportId;
        private int weekNumber;
        private LocalDate submittedAt;
        private String summary;
        private String status;
    }
}

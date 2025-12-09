package com.j3s.yobuddy.domain.kpi.results.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.j3s.yobuddy.domain.kpi.goals.entity.KpiGoals;
import com.j3s.yobuddy.domain.task.service.UserTaskQueryService;
import com.j3s.yobuddy.domain.training.dto.response.UserTrainingsResponse;
import com.j3s.yobuddy.domain.training.service.UserTrainingService;
import com.j3s.yobuddy.domain.weeklyReport.dto.response.WeeklyReportSummaryResponse;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport.WeeklyReportStatus;
import com.j3s.yobuddy.domain.weeklyReport.service.WeeklyReportService;

@Component
public class DefaultKpiScoreCalculator implements KpiScoreCalculator {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final int SCALE = 2;

    @Autowired
    private UserTrainingService userTrainingService;      // 교육 이수율
    @Autowired
    private UserTaskQueryService userTaskQueryService;    // 과제 제출률/품질
    @Autowired
    private WeeklyReportService weeklyReportService;      // 주간보고

    @Override
    public BigDecimal computeScore(Long userId, Long departmentId, KpiGoals kpiGoals) {
        if (userId == null || kpiGoals == null) {
            return BigDecimal.ZERO;
        }

        int target = kpiGoals.getTargetValue();
        if (target <= 0) {
            return BigDecimal.ZERO;
        }

        int category = kpiGoals.getKpiCategoryId().intValue();

        return switch (category) {
            case 1 -> computeTrainingCompletionScore(userId, target);    // 교육 이수율
            case 2 -> computeTaskSubmissionScore(userId, target);        // 과제 제출률
            case 3 -> computeTaskQualityScore(userId, target);           // 과제 품질
            case 4 -> computeWeeklyReportScore(userId);                  // 주간보고 제때 제출률
            case 5 -> computeAttendanceScore(userId);                    // 교육 출석률
            case 6 -> computeTeamCustomScore(userId, departmentId, kpiGoals); // 팀 고유 평가(임시 0)
            default -> BigDecimal.ZERO;
        };
    }

    // ============ 공통 유틸 ============

    /**
     * 예: actualPercent: 실제 퍼센트(0~100), target: 목표 퍼센트(예: 90)
     * result = actualPercent / target * 100 → 목표 달성률(%) 0~100 범위로 클램프
     */
    private BigDecimal percentageOfTarget(BigDecimal actualPercent, int target) {
        if (actualPercent == null || target <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal result = actualPercent
            .divide(BigDecimal.valueOf(target), SCALE + 2, RoundingMode.HALF_UP)
            .multiply(HUNDRED);

        return clampPercent(result);
    }

    /**
     * 0~100 범위로 잘라서 리턴
     */
    private BigDecimal clampPercent(BigDecimal value) {
        if (value == null) return BigDecimal.ZERO;

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP);
        }
        if (value.compareTo(HUNDRED) > 0) {
            return HUNDRED.setScale(SCALE, RoundingMode.HALF_UP);
        }
        return value.setScale(SCALE, RoundingMode.HALF_UP);
    }

    // ============ 카테고리별 계산 ============

    /**
     * 카테고리 1: 교육 이수율
     * userTrainingService.calculateCompletionRate(userId) 가 0~100(%) 리턴한다고 가정.
     */
    private BigDecimal computeTrainingCompletionScore(Long userId, int target) {
        BigDecimal completionRate = userTrainingService.calculateCompletionRate(userId); // 0~100
        return percentageOfTarget(completionRate, target);
    }

    /**
     * 카테고리 2: 과제 제출률
     * userTaskQueryService.calculateCompletionRate(userId) 가 0~100(%) 리턴한다고 가정.
     */
    private BigDecimal computeTaskSubmissionScore(Long userId, int target) {
        BigDecimal submissionRate = userTaskQueryService.calculateCompletionRate(userId); // 0~100
        return percentageOfTarget(submissionRate, target);
    }

    /**
     * 카테고리 3: 과제 품질
     * 일단 과제 품질도 completionRate 기반으로, 동일하게 '목표 대비'로 계산.
     * 나중에 별도 품질 지표가 생기면 이 메서드만 교체하면 됨.
     */
    private BigDecimal computeTaskQualityScore(Long userId, int target) {
        BigDecimal qualityScore = userTaskQueryService.calculateCompletionRate(userId); // 0~100 가정
        return percentageOfTarget(qualityScore, target);
    }

    /**
     * 카테고리 4: 주간보고
     * - 기준: "마감일(endDate) 이전/당일에 제출한 보고서 비율"
     * - status: SUBMITTED / REVIEWED / FEEDBACK_OVERDUE 만 유효로 간주
     */
    private BigDecimal computeWeeklyReportScore(Long userId) {
        List<WeeklyReportSummaryResponse> weeklyReports =
            weeklyReportService.getWeeklyReports(userId, null, null).toList();

        long totalReports = weeklyReports.size();
        if (totalReports == 0) {
            return BigDecimal.ZERO;
        }

        long submittedOnTime = weeklyReports.stream()
            .filter(r -> {
                WeeklyReportStatus s = r.getStatus();
                boolean statusOk = s == WeeklyReportStatus.SUBMITTED
                    || s == WeeklyReportStatus.REVIEWED
                    || s == WeeklyReportStatus.FEEDBACK_OVERDUE;

                if (!statusOk) return false;
                if (r.getSubmittedAt() == null || r.getEndDate() == null) return false;

                // 마감일 이전/당일 제출
                return !r.getSubmittedAt().toLocalDate().isAfter(r.getEndDate());
            })
            .count();

        BigDecimal ratio = BigDecimal.valueOf(submittedOnTime)
            .divide(BigDecimal.valueOf(totalReports), SCALE + 2, RoundingMode.HALF_UP)
            .multiply(HUNDRED);

        return clampPercent(ratio);
    }

    /**
     * 카테고리 5: 출석률 (OFFLINE 교육 기준)
     * - OFFLINE 교육 전체 중 status == COMPLETED 인 비율
     */
    private BigDecimal computeAttendanceScore(Long userId) {
        UserTrainingsResponse offlineTrainings =
            userTrainingService.getUserTrainings(userId, null, "OFFLINE");

        if (offlineTrainings == null || offlineTrainings.getTrainings() == null) {
            return BigDecimal.ZERO;
        }

        long totalOffline = offlineTrainings.getTrainings().size();
        if (totalOffline == 0) {
            return BigDecimal.ZERO;
        }

        long attendedOffline = offlineTrainings.getTrainings().stream()
            .filter(t -> {
                String status = t.getStatus();
                return status != null && "COMPLETED".equalsIgnoreCase(status.trim());
            })
            .count();

        BigDecimal ratio = BigDecimal.valueOf(attendedOffline)
            .divide(BigDecimal.valueOf(totalOffline), SCALE + 2, RoundingMode.HALF_UP)
            .multiply(HUNDRED);

        return clampPercent(ratio);
    }

    /**
     * 카테고리 6: 팀 고유 평가
     * - 현재는 로직 미정이라 0 리턴.
     * - 나중에 부서별/팀별 커스텀 로직을 여기에만 추가하면 됨.
     */
    private BigDecimal computeTeamCustomScore(Long userId, Long departmentId, KpiGoals kpiGoals) {
        // TODO: 팀 고유 평가 로직 정의되면 여기 구현
        return BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP);
    }
}

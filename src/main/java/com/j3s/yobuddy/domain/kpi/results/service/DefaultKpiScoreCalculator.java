package com.j3s.yobuddy.domain.kpi.results.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.j3s.yobuddy.domain.kpi.goals.entity.KpiGoals;
import com.j3s.yobuddy.domain.kpi.goals.repository.KpiGoalsRepository;
import com.j3s.yobuddy.domain.kpi.results.dto.request.KpiResultsRequest;
import com.j3s.yobuddy.domain.task.service.UserTaskQueryService;
import com.j3s.yobuddy.domain.training.service.UserTrainingService;
import com.j3s.yobuddy.domain.weeklyReport.service.WeeklyReportService;
import com.j3s.yobuddy.domain.weeklyReport.dto.response.WeeklyReportSummaryResponse;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport.WeeklyReportStatus;



/**
 * 기본 점수 계산기: 현재는 간단한 규칙을 사용합니다.
 * - 요청에 명시된 score가 있으면 그 값을 사용
 * - 그렇지 않으면 achievedValue를 기본 score로 사용하되 0~100 범위로 클램프
 *
 * 필요하면 더 복잡한 로직(가중치, 목표치 대비 비율 등)으로 교체하세요.
 */
@Component
public class DefaultKpiScoreCalculator implements KpiScoreCalculator {

    @Autowired
    private KpiGoalsRepository kpiGoalsRepository;
    @Autowired
    private UserTrainingService userTrainingService; // 교육이수율 계산용 서비스
    @Autowired
    private UserTaskQueryService userTaskQueryService; // 과제 제출률 계산용 서비스
    @Autowired
    private WeeklyReportService weeklyReportService; // 주간보고 계산용 서비스
    @Override
    public BigDecimal computeScore(KpiResultsRequest request) {
        List<KpiGoals> kpiGoalList = request.getDepartmentId() != null
            ? kpiGoalsRepository.findByDepartmentIdAndIsDeletedFalse(request.getDepartmentId())
            : List.of();
        for (KpiGoals kpiGoals : kpiGoalList) {
            Long userId = request.getUserId();
            int target = kpiGoals.getTargetValue();
            BigDecimal weight = kpiGoals.getWeight();
            switch (kpiGoals.getKpiCategoryId().intValue()) {
                case 3: // 교육이수율
                    if (userId != null) {
                        // score 계산 로직 예시: 교육이수율 * 가중치
                        BigDecimal score = userTrainingService.calculateCompletionRate(userId);
                        if (target == 0) {
                            return BigDecimal.ZERO;
                        }
                        BigDecimal result = score
                            .divide(BigDecimal.valueOf(target), 2, java.math.RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));
                        return result;
                    }
                    break;
                case 4: // 과제 제출률 
                    if (userId != null) {
                        // score 계산 로직 예시: 과제제출 * 가중치
                        BigDecimal score = userTaskQueryService.calculateCompletionRate(userId);
                        if (target == 0) {
                            return BigDecimal.ZERO;
                        }
                        BigDecimal result = score
                            .divide(BigDecimal.valueOf(target), 2, java.math.RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));
                        return result;
                    }
                    break;
                case 5: // 과제 품질
                    if (userId != null) {
                        BigDecimal score = userTaskQueryService.calculateCompletionRate(userId);
                        if (target == 0) {
                            return BigDecimal.ZERO;
                        }
                        BigDecimal result = score
                            .divide(BigDecimal.valueOf(target), 2, java.math.RoundingMode.HALF_UP);
                        return result;
                    }
                    break;
                case 6: // 주간보고
                    if (userId != null) {
                        List<WeeklyReportSummaryResponse> weeklyReports = weeklyReportService.getWeeklyReports(userId, null, null).toList();
                        long totalReports = weeklyReports.size();
                        long submittedReports = weeklyReports.stream().filter(r -> {
                            WeeklyReportStatus s = r.getStatus();
                            return s != null && s == WeeklyReportStatus.SUBMITTED || s == WeeklyReportStatus.REVIEWED || s == WeeklyReportStatus.FEEDBACK_OVERDUE;
                        }).count();
                        if (totalReports == 0) {
                            return BigDecimal.ZERO;
                        }
                        BigDecimal result = BigDecimal.valueOf(submittedReports)
                            .divide(BigDecimal.valueOf(totalReports), 2, java.math.RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));
                        return result;
                    }
                    break;
                case 7: // 출석률
                    if (userId != null) {
                        // TODO: implement attendance rate calculation
                    }
                    break;
                case 8: // 팀 고유 평가
                    if (userId != null) {
                        // TODO: implement team-specific evaluation
                    }
                    break;    
                default:
                    break;
            }
        }
        return BigDecimal.ZERO;
    }
}

package com.j3s.yobuddy.domain.kpi.results.service;

import com.j3s.yobuddy.domain.department.entity.Department;
import com.j3s.yobuddy.domain.department.repository.DepartmentRepository;
import com.j3s.yobuddy.domain.kpi.goals.entity.KpiGoals;
import com.j3s.yobuddy.domain.kpi.goals.repository.KpiGoalsRepository;
import com.j3s.yobuddy.domain.kpi.results.dto.dashboard.*;
import com.j3s.yobuddy.domain.kpi.results.entity.KpiResults;
import com.j3s.yobuddy.domain.kpi.results.repository.KpiDashboardQueryRepository;
import com.j3s.yobuddy.domain.kpi.results.repository.KpiResultsRepository;
import com.j3s.yobuddy.domain.mentor.mentoring.entity.MentoringSession;
import com.j3s.yobuddy.domain.mentor.mentoring.entity.MentoringStatus;
import com.j3s.yobuddy.domain.mentor.mentoring.repository.MentoringSessionRepository;
import com.j3s.yobuddy.domain.onboarding.repository.OnboardingProgramRepository;
import com.j3s.yobuddy.domain.user.entity.User;
import com.j3s.yobuddy.domain.user.repository.UserRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KpiDashboardService {

    private final UserRepository userRepository;
    private final KpiGoalsRepository kpiGoalsRepository;
    private final KpiResultsRepository kpiResultsRepository;
    private final DepartmentRepository departmentRepository;

    // 멘토링용 Repository
    private final MentoringSessionRepository mentoringSessionRepository;
    private final OnboardingProgramRepository onboardingProgramRepository;

    private final KpiDashboardQueryRepository dashboardQueryRepository;

    private static final BigDecimal PASS_THRESHOLD = BigDecimal.valueOf(60);

    @Transactional(readOnly = true)
    public DashboardOverviewResponse getOverviewByPeriod(LocalDate start, LocalDate end) {
        return dashboardQueryRepository.fetchOverviewByPeriod(start, end);
    }

    @Transactional(readOnly = true)
    public KpiDashboardResponse getDashboard(Long departmentId) {

        Department dept = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new IllegalArgumentException("Department not found id=" + departmentId));

        List<User> users = userRepository.findByDepartment_DepartmentIdAndIsDeletedFalse(departmentId);
        List<KpiGoals> goals = kpiGoalsRepository.findByDepartmentIdAndIsDeletedFalse(departmentId);
        List<KpiResults> results = kpiResultsRepository.findByDepartmentIdAndIsDeletedFalse(departmentId);

        // 목표 가중치 Map
        Map<Long, BigDecimal> weightMap = goals.stream()
            .collect(Collectors.toMap(
                KpiGoals::getKpiGoalId,
                g -> g.getWeight() == null ? BigDecimal.ZERO : g.getWeight()
            ));

        // 유저별 KPI 결과 그룹화
        Map<Long, List<KpiResults>> resultsByUser =
            results.stream().collect(Collectors.groupingBy(KpiResults::getUserId));

        List<UserDashboardDto> userDtos = new ArrayList<>();
        long pass = 0;

        for (User user : users) {
            List<KpiResults> userResults = resultsByUser.getOrDefault(user.getUserId(), List.of());
            BigDecimal totalScore = calcTotal(userResults, weightMap);

            boolean isPass = totalScore.compareTo(PASS_THRESHOLD) >= 0;
            if (isPass) pass++;

            List<UserKpiResultDto> resultDtos = userResults.stream()
                .map(UserKpiResultDto::from)
                .toList();

            userDtos.add(UserDashboardDto.of(user, totalScore, isPass, resultDtos));
        }

        SummaryDto summary = SummaryDto.builder()
            .pass(pass)
            .fail(users.size() - pass)
            .totalUsers(users.size())
            .build();

        MentoringSummaryDto mentoring = buildMentoringSummary(departmentId);

        // KPI 차트 구성
        DashboardChartDto chart = buildCharts(results, weightMap, goals);

        return KpiDashboardResponse.builder()
            .departmentId(dept.getDepartmentId())
            .departmentName(dept.getName())
            .goals(goals.stream().map(KpiGoalDto::from).toList())
            .users(userDtos)
            .summary(summary)
            .mentoring(mentoring)
            .chart(chart)
            .build();
    }

    private BigDecimal calcTotal(List<KpiResults> results, Map<Long, BigDecimal> weightMap) {
        BigDecimal total = BigDecimal.ZERO;

        for (KpiResults r : results) {
            BigDecimal w = weightMap.getOrDefault(r.getKpiGoalId(), BigDecimal.ZERO);
            if (r.getScore() != null) {
                total = total.add(r.getScore().multiply(w));
            }
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private MentoringSummaryDto buildMentoringSummary(Long deptId) {

        var programs = onboardingProgramRepository
            .findByDepartment_DepartmentIdAndDeletedFalse(deptId);

        if (programs == null || programs.isEmpty()) {
            return MentoringSummaryDto.empty();
        }

        var programIds = programs.stream()
            .map(p -> p.getProgramId())
            .toList();

        List<MentoringSession> sessions =
            mentoringSessionRepository.findByProgram_ProgramIdInAndDeletedFalse(programIds);

        long total = sessions.size();

        if (total == 0) return MentoringSummaryDto.empty();

        long completed = sessions.stream().filter(s -> s.getStatus() == MentoringStatus.COMPLETED).count();
        long scheduled = sessions.stream().filter(s -> s.getStatus() == MentoringStatus.SCHEDULED).count();
        long cancelled = sessions.stream().filter(s -> s.getStatus() == MentoringStatus.CANCELLED).count();
        long noShow = sessions.stream().filter(s -> s.getStatus() == MentoringStatus.NO_SHOW).count();

        return MentoringSummaryDto.builder()
            .total(total)
            .completed(completed)
            .scheduled(scheduled)
            .cancelled(cancelled)
            .noShow(noShow)
            .build();
    }

    private DashboardChartDto buildCharts(
        List<KpiResults> results,
        Map<Long, BigDecimal> weightMap,
        List<KpiGoals> goals
    ) {

        // 연도별 유저별 누적점수
        Map<Integer, Map<Long, BigDecimal>> yearUserScores = new HashMap<>();

        for (KpiResults r : results) {
            if (r.getEvaluatedAt() == null || r.getScore() == null) continue;

            int year = r.getEvaluatedAt().getYear();
            long userId = r.getUserId();

            BigDecimal w = weightMap.getOrDefault(r.getKpiGoalId(), BigDecimal.ZERO);
            BigDecimal contrib = r.getScore().multiply(w);

            yearUserScores
                .computeIfAbsent(year, y -> new HashMap<>())
                .merge(userId, contrib, BigDecimal::add);
        }

        List<Integer> years = yearUserScores.keySet().stream().sorted().toList();
        List<BigDecimal> achievement = new ArrayList<>();

        for (Integer y : years) {
            Map<Long, BigDecimal> map = yearUserScores.get(y);

            BigDecimal avg = map.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(map.size()), 2, RoundingMode.HALF_UP);

            achievement.add(avg);
        }

        // 레이더 차트
        List<RadarPointDto> radar = goals.stream().map(g -> {

            List<BigDecimal> scores = results.stream()
                .filter(r -> Objects.equals(r.getKpiGoalId(), g.getKpiGoalId()))
                .map(KpiResults::getScore)
                .filter(Objects::nonNull)
                .toList();

            BigDecimal avg = scores.isEmpty()
                ? BigDecimal.ZERO
                : scores.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(scores.size()), 2, RoundingMode.HALF_UP);

            return new RadarPointDto(
                g.getKpiGoalId(),
                g.getDescription(),
                avg
            );

        }).toList();

        return DashboardChartDto.builder()
            .years(years)
            .achievementPerYear(achievement)
            .radar(radar)
            .build();
    }
}

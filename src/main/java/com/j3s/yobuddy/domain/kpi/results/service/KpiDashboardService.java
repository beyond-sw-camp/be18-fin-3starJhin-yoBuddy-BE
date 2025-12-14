package com.j3s.yobuddy.domain.kpi.results.service;

import com.j3s.yobuddy.common.dto.FileResponse;
import com.j3s.yobuddy.domain.department.entity.Department;
import com.j3s.yobuddy.domain.department.repository.DepartmentRepository;
import com.j3s.yobuddy.domain.file.entity.RefType;
import com.j3s.yobuddy.domain.file.repository.FileRepository;
import com.j3s.yobuddy.domain.kpi.goals.entity.KpiGoals;
import com.j3s.yobuddy.domain.kpi.goals.repository.KpiGoalsRepository;
import com.j3s.yobuddy.domain.kpi.results.dto.dashboard.*;
import com.j3s.yobuddy.domain.kpi.results.entity.KpiResults;
import com.j3s.yobuddy.domain.kpi.results.repository.KpiDashboardQueryRepository;
import com.j3s.yobuddy.domain.kpi.results.repository.KpiResultsRepository;
import com.j3s.yobuddy.domain.mentor.mentoring.entity.MentoringSession;
import com.j3s.yobuddy.domain.mentor.mentoring.entity.MentoringStatus;
import com.j3s.yobuddy.domain.mentor.mentoring.repository.MentoringSessionRepository;
import com.j3s.yobuddy.domain.onboarding.entity.OnboardingProgram;
import com.j3s.yobuddy.domain.onboarding.repository.OnboardingProgramRepository;
import com.j3s.yobuddy.domain.user.entity.User;
import com.j3s.yobuddy.domain.user.repository.UserRepository;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport;
import com.j3s.yobuddy.domain.weeklyReport.repository.WeeklyReportRepository;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class KpiDashboardService {

    private final UserRepository userRepository;
    private final KpiGoalsRepository kpiGoalsRepository;
    private final KpiResultsRepository kpiResultsRepository;
    private final DepartmentRepository departmentRepository;

    private final MentoringSessionRepository mentoringSessionRepository;
    private final OnboardingProgramRepository onboardingProgramRepository;

    private final KpiDashboardQueryRepository dashboardQueryRepository;

    private final WeeklyReportRepository weeklyReportRepository;
    private final FileRepository fileRepository;

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

        Map<Long, BigDecimal> weightMap = goals.stream()
            .collect(Collectors.toMap(
                KpiGoals::getKpiGoalId,
                g -> g.getWeight() == null ? BigDecimal.ZERO : g.getWeight()
            ));

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
            if (r.getAchievedValue() != null) {
                total = total.add(r.getAchievedValue().multiply(w));
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
            .map(OnboardingProgram::getProgramId)
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

        Map<Integer, Map<Long, BigDecimal>> yearUserScores = new HashMap<>();

        for (KpiResults r : results) {
            if (r.getEvaluatedAt() == null || r.getAchievedValue() == null) continue;

            int year = r.getEvaluatedAt().getYear();
            long userId = r.getUserId();

            BigDecimal w = weightMap.getOrDefault(r.getKpiGoalId(), BigDecimal.ZERO);
            BigDecimal contrib = r.getAchievedValue().multiply(w);

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

        List<RadarPointDto> radar = goals.stream().map(g -> {
            List<BigDecimal> scores = results.stream()
                .filter(r -> Objects.equals(r.getKpiGoalId(), g.getKpiGoalId()))
                .map(KpiResults::getAchievedValue)
                .filter(Objects::nonNull)
                .toList();

            BigDecimal avg = scores.isEmpty()
                ? BigDecimal.ZERO
                : scores.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(scores.size()), 2, RoundingMode.HALF_UP);

            return new RadarPointDto(g.getKpiGoalId(), g.getDescription(), avg);
        }).toList();

        return DashboardChartDto.builder()
            .years(years)
            .achievementPerYear(achievement)
            .radar(radar)
            .build();
    }

    @Transactional(readOnly = true)
    public UserKpiDetailResponse getUserDetail(Long userId, Long departmentId) {

        Department dept = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new IllegalArgumentException("Department not found id=" + departmentId));

        User u = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found id=" + userId));

        Long programId = dashboardQueryRepository.fetchLatestProgramIdForUser(userId, departmentId);

        if (programId == null) {
            programId = onboardingProgramRepository
                .findTopByDepartment_DepartmentIdAndDeletedFalseOrderByStartDateDesc(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Program not found for department=" + departmentId))
                .getProgramId();
        }

        final Long finalProgramId = programId;

        OnboardingProgram p = onboardingProgramRepository.findById(finalProgramId)
            .orElseThrow(() -> new IllegalArgumentException("Program not found id=" + finalProgramId));

        // 레이더
        var userRadar = dashboardQueryRepository.fetchUserGoalRadar(userId, departmentId, programId);
        var deptRadar = dashboardQueryRepository.fetchDeptGoalRadar(departmentId, programId);

        // 종합 KPI 점수: userRadar(goal별 최신) 기반
        List<KpiGoals> goals = kpiGoalsRepository.findByDepartmentIdAndIsDeletedFalse(departmentId);
        Map<Long, BigDecimal> weightMap = goals.stream()
            .collect(Collectors.toMap(
                KpiGoals::getKpiGoalId,
                g -> g.getWeight() == null ? BigDecimal.ZERO : g.getWeight()
            ));

        BigDecimal total = BigDecimal.ZERO;
        for (var item : userRadar) {
            BigDecimal w = weightMap.getOrDefault(item.getKpiGoalId(), BigDecimal.ZERO);
            BigDecimal s = item.getScore() == null ? BigDecimal.ZERO : item.getScore();
            total = total.add(s.multiply(w));
        }
        BigDecimal totalKpiScore = total.setScale(2, RoundingMode.HALF_UP);

        var taskSummary = dashboardQueryRepository.fetchUserTaskProgress(userId, programId);
        var eduSummary = dashboardQueryRepository.fetchUserEducationProgress(userId, programId);
        var avgTaskScore = dashboardQueryRepository.fetchUserAvgTaskScore(userId, programId);
        var mentoringCount = dashboardQueryRepository.fetchUserMentoringCompletedCount(userId, programId);

        LocalDate start = p.getStartDate();
        LocalDate end = p.getEndDate();

        var weekly = weeklyReportRepository.getByMenteeId(userId).stream()
            .filter(wr -> {
                if (start == null || end == null) return true; // 기간 없으면 필터 안 함
                LocalDate d = wr.getCreatedAt().toLocalDate();
                return !d.isBefore(start) && !d.isAfter(end);
            })
            .sorted(Comparator.comparing(WeeklyReport::getCreatedAt).reversed())
            .map(w -> UserKpiDetailResponse.WeeklyCard.builder()
                .weeklyReportId(w.getWeeklyReportId())
                .weekNumber(w.getWeekNumber())
                .submittedAt(w.getCreatedAt().toLocalDate())
                .summary(w.getLearnings())
                .status(w.getStatus().name())
                .build())
            .toList();

        String profileImageUrl = fileRepository
            .findByRefTypeAndRefId(RefType.USER_PROFILE, userId)
            .stream()
            .findFirst()
            .map(FileResponse::from)
            .map(FileResponse::getUrl)
            .orElse(null);

        return UserKpiDetailResponse.builder()
            .user(UserKpiDetailResponse.UserInfo.builder()
                .userId(u.getUserId())
                .name(u.getName())
                .departmentName(dept.getName())
                .profileImageUrl(profileImageUrl)
                .build())
            .program(UserKpiDetailResponse.ProgramInfo.builder()
                .programId(programId)
                .name(p.getName())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .build())
            .mentoringCount(mentoringCount)
            .avgTaskScore(avgTaskScore)
            .totalKpiScore(totalKpiScore)
            .userRadar(userRadar)
            .deptRadar(deptRadar)
            .task(taskSummary)
            .education(eduSummary)
            .weeklyReports(weekly)
            .build();
    }
}

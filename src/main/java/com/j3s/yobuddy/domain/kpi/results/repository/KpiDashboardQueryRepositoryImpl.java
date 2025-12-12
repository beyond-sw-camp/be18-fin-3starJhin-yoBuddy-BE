package com.j3s.yobuddy.domain.kpi.results.repository;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.types.Projections.list;

import com.j3s.yobuddy.domain.department.entity.QDepartment;
import com.j3s.yobuddy.domain.kpi.goals.entity.QKpiGoals;
import com.j3s.yobuddy.domain.kpi.results.dto.dashboard.*;
import com.j3s.yobuddy.domain.kpi.results.entity.QKpiResults;
import com.j3s.yobuddy.domain.mentor.mentoring.entity.QMentoringSession;
import com.j3s.yobuddy.domain.onboarding.entity.OnboardingProgram;
import com.j3s.yobuddy.domain.onboarding.entity.QOnboardingProgram;
import com.j3s.yobuddy.domain.task.entity.QUserTask;
import com.j3s.yobuddy.domain.training.entity.QUserTraining;
import com.j3s.yobuddy.domain.user.entity.QUser;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class KpiDashboardQueryRepositoryImpl implements KpiDashboardQueryRepository {

    private final JPAQueryFactory query;

    private final QOnboardingProgram program = QOnboardingProgram.onboardingProgram;
    private final QUser user = QUser.user;
    private final QDepartment dept = QDepartment.department;
    private final QKpiResults kpi = QKpiResults.kpiResults;
    private final QKpiGoals goal = QKpiGoals.kpiGoals;
    private final QMentoringSession mentoring = QMentoringSession.mentoringSession;
    private final QUserTask task = QUserTask.userTask;
    private final QUserTraining training = QUserTraining.userTraining;

    // ------------------------ PERIOD DASHBOARD ------------------------
    @Override
    public DashboardOverviewResponse fetchOverviewByPeriod(LocalDate start, LocalDate end) {

        // ① 기간 내 모든 온보딩 프로그램 조회
        List<OnboardingProgram> programs = query
            .selectFrom(program)
            .where(
                program.deleted.isFalse(),
                program.startDate.goe(start),
                program.endDate.loe(end)
            )
            .fetch();

        if (programs.isEmpty()) {
            return DashboardOverviewResponse.builder()
                .summary(TopSummaryDto.builder()
                    .periodLabel(makePeriodLabel(start))
                    .startDate(start)
                    .endDate(end)
                    .build())
                .departments(List.of())
                .radar(RadarDashboardDto.builder().departments(List.of()).build())
                .build();
        }

        List<Long> programIds = programs.stream()
            .map(p -> p.getProgramId())
            .toList();

        List<Long> departmentIds = programs.stream()
            .map(p -> p.getDepartment().getDepartmentId())
            .distinct()
            .toList();

        // ② Summary 계산
        TopSummaryDto summary = fetchTopSummary(programIds, start, end);

        // ③ 부서별 성과 집계
        List<DepartmentDashboardDto> departments =
            fetchDepartmentStats(programIds);

        // ④ Radar 생성
        RadarDashboardDto radar =
            fetchRadar(programIds, departmentIds, summary.getPeriodLabel());

        return DashboardOverviewResponse.builder()
            .summary(summary)
            .departments(departments)
            .radar(radar)
            .build();
    }

    private String makePeriodLabel(LocalDate start) {
        return start.getYear() + "년 " +
            (start.getMonthValue() <= 6 ? "상반기" : "하반기");
    }

    // ------------------------ SUMMARY ------------------------
    private TopSummaryDto fetchTopSummary(List<Long> programIds,
        LocalDate start,
        LocalDate end) {

        Long newUsers = query
            .select(user.countDistinct())
            .from(user)
            .where(
                user.isDeleted.isFalse(),
                user.joinedAt.between(start.atStartOfDay(), end.atTime(23, 59))
            )
            .fetchOne();

        Long totalMentoring = query
            .select(mentoring.count())
            .from(mentoring)
            .where(
                mentoring.deleted.isFalse(),
                mentoring.program.programId.in(programIds)
            )
            .fetchOne();

        Double avgKpi = query
            .select(kpi.score.avg())
            .from(kpi)
            .fetchOne();

        Double avgTask = query
            .select(task.grade.avg())
            .from(task)
            .where(task.deleted.isFalse())
            .fetchOne();

        return TopSummaryDto.builder()
            .periodLabel(makePeriodLabel(start))
            .startDate(start)
            .endDate(end)
            .newUserCount(newUsers == null ? 0 : newUsers)
            .totalMentoringCount(totalMentoring == null ? 0 : totalMentoring)
            .avgKpiScore(avgKpi == null ? BigDecimal.ZERO : BigDecimal.valueOf(avgKpi).setScale(1, RoundingMode.HALF_UP))
            .avgTaskScore(avgTask == null ? BigDecimal.ZERO : BigDecimal.valueOf(avgTask).setScale(1, RoundingMode.HALF_UP))
            .build();
    }

    // ------------------------ DEPARTMENT METRICS ------------------------
    private List<DepartmentDashboardDto> fetchDepartmentStats(List<Long> programIds) {

        Map<Long, DepartmentDashboardDto.DepartmentDashboardDtoBuilder> map = new HashMap<>();

        // KPI 평균 + 목표 KPI
        List<Tuple> tuples = query
            .select(
                dept.departmentId,
                dept.name,
                kpi.score.avg(),
                goal.targetValue.avg()
            )
            .from(user)
            .join(user.department, dept)
            .leftJoin(kpi).on(kpi.userId.eq(user.userId))
            .leftJoin(goal).on(goal.departmentId.eq(dept.departmentId))
            .groupBy(dept.departmentId, dept.name)
            .fetch();

        for (Tuple t : tuples) {
            map.put(
                t.get(dept.departmentId),
                DepartmentDashboardDto.builder()
                    .departmentId(t.get(dept.departmentId))
                    .departmentName(t.get(dept.name))
                    .currentAvgKpi(BigDecimal.valueOf(Optional.ofNullable(t.get(kpi.score.avg())).orElse(0.0)))
                    .targetAvgKpi(BigDecimal.valueOf(Optional.ofNullable(t.get(goal.targetValue.avg())).orElse(0.0)))
            );
        }

        // 멘토링 횟수
        List<Tuple> mentoringStats = query
            .select(
                program.department.departmentId,
                mentoring.count()
            )
            .from(mentoring)
            .join(mentoring.program, program)
            .where(
                mentoring.deleted.isFalse(),
                mentoring.program.programId.in(programIds)
            )
            .groupBy(program.department.departmentId)
            .fetch();

        for (Tuple t : mentoringStats) {
            map.get(t.get(program.department.departmentId))
                .mentoringCount(t.get(mentoring.count()));
        }

        return map.values().stream()
            .map(DepartmentDashboardDto.DepartmentDashboardDtoBuilder::build)
            .toList();
    }

    // ------------------------ RADAR ------------------------
    private RadarDashboardDto fetchRadar(
        List<Long> programIds,
        List<Long> departmentIds,
        String periodLabel
    ) {

        // 1) 부서별 KPI Goals 모두 불러오기 (Service 방식 기반)
        List<KpiGoalDto> allGoals = query
            .select(Projections.fields(
                KpiGoalDto.class,
                goal.kpiGoalId.as("kpiGoalId"),
                goal.description.as("description"),
                goal.targetValue.as("targetValue"),
                goal.weight.as("weight"),
                goal.kpiCategoryId.as("kpiCategoryId"),
                goal.departmentId.as("departmentId")
            ))
            .from(goal)
            .where(
                goal.departmentId.in(departmentIds),
                goal.isDeleted.isFalse()
            )
            .orderBy(goal.departmentId.asc(), goal.kpiGoalId.asc())
            .fetch();

        // 2) KPI Results 가져오기 (해당 기간, 해당 부서 대상)
        List<Tuple> resultTuples = query
            .select(
                kpi.departmentId,
                kpi.kpiGoalId,
                kpi.score
            )
            .from(kpi)
            .where(
                kpi.departmentId.in(departmentIds),
                kpi.isDeleted.isFalse()
            )
            .fetch();

        // ▼ 결과를 Map<departmentId, Map<goalId, List<score>>> 구조로 묶음
        Map<Long, Map<Long, List<BigDecimal>>> scoreMap = new HashMap<>();

        for (Tuple t : resultTuples) {
            Long deptId = t.get(kpi.departmentId);
            Long goalId = t.get(kpi.kpiGoalId);
            BigDecimal score = t.get(kpi.score);

            if (deptId == null || goalId == null || score == null) continue;

            scoreMap
                .computeIfAbsent(deptId, x -> new HashMap<>())
                .computeIfAbsent(goalId, x -> new ArrayList<>())
                .add(score);
        }

        // 3) 부서 정보 조회
        List<Tuple> deptRows = query
            .select(dept.departmentId, dept.name)
            .from(dept)
            .where(dept.departmentId.in(departmentIds))
            .fetch();

        // 4) 최종 Radar 데이터 생성 (Service와 동일한 방식)
        List<RadarDepartmentScoreDto> radarDepartments = new ArrayList<>();

        for (Tuple d : deptRows) {
            Long deptId = d.get(dept.departmentId);
            String deptName = d.get(dept.name);

            // 이 부서에 속한 Goal 6개 필터링
            List<KpiGoalDto> deptGoals = allGoals.stream()
                .filter(g -> g.getDepartmentId().equals(deptId))
                .toList();

            // Service 방식으로 점수 계산
            List<RadarPointDto> points = deptGoals.stream()
                .map(g -> {
                    List<BigDecimal> scores = scoreMap
                        .getOrDefault(deptId, Map.of())
                        .getOrDefault(g.getKpiGoalId(), List.of());

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
                })
                .toList();

            radarDepartments.add(
                new RadarDepartmentScoreDto(deptId, deptName, points)
            );
        }

        return RadarDashboardDto.builder()
            .periodLabel(periodLabel)
            .departments(radarDepartments)
            .build();
    }

}

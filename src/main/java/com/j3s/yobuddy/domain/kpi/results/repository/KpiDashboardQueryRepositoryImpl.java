package com.j3s.yobuddy.domain.kpi.results.repository;

import com.j3s.yobuddy.domain.department.entity.QDepartment;
import com.j3s.yobuddy.domain.kpi.goals.entity.QKpiGoals;
import com.j3s.yobuddy.domain.kpi.results.dto.dashboard.*;
import com.j3s.yobuddy.domain.kpi.results.dto.dashboard.UserKpiDetailResponse.ProgressSummary;
import com.j3s.yobuddy.domain.kpi.results.entity.QKpiResults;
import com.j3s.yobuddy.domain.mentor.mentoring.entity.MentoringStatus;
import com.j3s.yobuddy.domain.mentor.mentoring.entity.QMentoringSession;
import com.j3s.yobuddy.domain.onboarding.entity.OnboardingProgram;
import com.j3s.yobuddy.domain.onboarding.entity.QOnboardingProgram;
import com.j3s.yobuddy.domain.task.entity.QUserTask;
import com.j3s.yobuddy.domain.training.entity.QUserTraining;
import com.j3s.yobuddy.domain.user.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import java.util.stream.Collectors;
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

        LocalDateTime startDt = start.atStartOfDay();
        LocalDateTime endDt = end.atTime(23, 59, 59);

        List<OnboardingProgram> programs = query
            .selectFrom(program)
            .where(
                program.deleted.isFalse(),
                program.startDate.goe(start),
                program.endDate.loe(end)
            )
            .fetch();

        List<Long> programIds = programs.stream()
            .map(OnboardingProgram::getProgramId)
            .toList();

        List<DepartmentDashboardDto> departments =
            fetchDepartmentStats(programIds, startDt, endDt);

        // 🔥 KPI 종합 점수 = 부서 KPI 평균
        BigDecimal overallKpiAvg = departments.isEmpty()
            ? BigDecimal.ZERO
            : departments.stream()
                .map(DepartmentDashboardDto::getCurrentAvgKpi)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(
                    BigDecimal.valueOf(departments.size()),
                    1,
                    RoundingMode.HALF_UP
                );

        TopSummaryDto summary =
            fetchTopSummary(programIds, startDt, endDt, start, end, overallKpiAvg);

        RadarDashboardDto radar =
            fetchRadar(
                departments.stream()
                    .map(DepartmentDashboardDto::getDepartmentId)
                    .toList(),
                startDt,
                endDt,
                summary.getPeriodLabel()
            );

        return DashboardOverviewResponse.builder()
            .summary(summary)
            .departments(departments)
            .radar(radar)
            .build();
    }

    @Override
    public List<UserKpiDetailResponse.RadarItem> fetchUserGoalRadar(Long userId, Long departmentId, Long programId) {

        Tuple period = query
            .select(program.startDate, program.endDate)
            .from(program)
            .where(program.programId.eq(programId), program.deleted.isFalse())
            .fetchOne();

        LocalDate start = period == null ? null : period.get(program.startDate);
        LocalDate end = period == null ? null : period.get(program.endDate);

        LocalDateTime startDt = (start == null) ? null : start.atStartOfDay();
        LocalDateTime endDt = (end == null) ? null : end.atTime(23, 59, 59);

        // 1) 해당 부서 goals
        List<Tuple> goals = query
            .select(goal.kpiGoalId, goal.description)
            .from(goal)
            .where(goal.departmentId.eq(departmentId), goal.isDeleted.isFalse())
            .orderBy(goal.kpiGoalId.asc())
            .fetch();

        // 2) 유저 KPI 결과(기간 내) 가져와서 goal별 latest만 남김
        BooleanBuilder where = new BooleanBuilder()
            .and(kpi.isDeleted.isFalse())
            .and(kpi.userId.eq(userId))
            .and(kpi.departmentId.eq(departmentId));

        if (startDt != null && endDt != null) {
            where.and(kpi.evaluatedAt.between(startDt, endDt));
        }

        List<Tuple> rows = query
            .select(kpi.kpiGoalId, kpi.achievedValue, kpi.evaluatedAt)
            .from(kpi)
            .where(where)
            .fetch();

        Map<Long, Tuple> latestByGoal = new HashMap<>();
        for (Tuple t : rows) {
            Long gid = t.get(kpi.kpiGoalId);
            LocalDateTime evaluatedAt = t.get(kpi.evaluatedAt);

            Tuple prev = latestByGoal.get(gid);
            if (prev == null) {
                latestByGoal.put(gid, t);
            } else {
                LocalDateTime prevAt = prev.get(kpi.evaluatedAt);
                if (prevAt == null || (evaluatedAt != null && evaluatedAt.isAfter(prevAt))) {
                    latestByGoal.put(gid, t);
                }
            }
        }

        List<UserKpiDetailResponse.RadarItem> result = new ArrayList<>();
        for (Tuple g : goals) {
            Long gid = g.get(goal.kpiGoalId);
            String label = g.get(goal.description);

            Tuple latest = latestByGoal.get(gid);
            BigDecimal score = (latest == null || latest.get(kpi.achievedValue) == null)
                ? BigDecimal.ZERO
                : latest.get(kpi.achievedValue);

            result.add(UserKpiDetailResponse.RadarItem.builder()
                .kpiGoalId(gid)
                .label(label)
                .score(score.setScale(2, RoundingMode.HALF_UP))
                .build());
        }
        return result;
    }

    @Override
    public List<UserKpiDetailResponse.RadarItem> fetchDeptGoalRadar(Long departmentId, Long programId) {

        Tuple period = query
            .select(program.startDate, program.endDate)
            .from(program)
            .where(program.programId.eq(programId), program.deleted.isFalse())
            .fetchOne();

        LocalDate start = period == null ? null : period.get(program.startDate);
        LocalDate end = period == null ? null : period.get(program.endDate);

        LocalDateTime startDt = (start == null) ? null : start.atStartOfDay();
        LocalDateTime endDt = (end == null) ? null : end.atTime(23, 59, 59);

        BooleanBuilder joinOn = new BooleanBuilder()
            .and(kpi.kpiGoalId.eq(goal.kpiGoalId))
            .and(kpi.departmentId.eq(departmentId))
            .and(kpi.isDeleted.isFalse());

        if (startDt != null && endDt != null) {
            joinOn.and(kpi.evaluatedAt.between(startDt, endDt));
        }

        List<Tuple> rows = query
            .select(goal.kpiGoalId, goal.description, kpi.achievedValue.avg())
            .from(goal)
            .leftJoin(kpi).on(joinOn)
            .where(goal.departmentId.eq(departmentId), goal.isDeleted.isFalse())
            .groupBy(goal.kpiGoalId, goal.description)
            .orderBy(goal.kpiGoalId.asc())
            .fetch();

        return rows.stream().map(t -> {
            Double avg = t.get(kpi.achievedValue.avg());
            BigDecimal v = (avg == null)
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP);

            return UserKpiDetailResponse.RadarItem.builder()
                .kpiGoalId(t.get(goal.kpiGoalId))
                .label(t.get(goal.description))
                .score(v)
                .build();
        }).toList();
    }

    @Override
    public ProgressSummary fetchUserTaskProgress(Long userId, Long programId) {

        List<Tuple> rows = query
            .select(task.status, task.submittedAt, task.programTask.dueDate)
            .from(task)
            .where(
                task.deleted.isFalse(),
                task.user.userId.eq(userId),
                task.programTask.onboardingProgram.programId.eq(programId)
            )
            .fetch();

        int completed = 0;
        int remaining = 0;
        int onTime = 0;
        int late = 0;

        for (Tuple t : rows) {
            var st = t.get(task.status);
            var submittedAt = t.get(task.submittedAt);
            var due = t.get(task.programTask.dueDate);

            boolean isCompleted = st != null &&
                (st.name().equals("SUBMITTED") || st.name().equals("GRADED") || st.name().equals("LATE"));
            boolean isRemaining = st != null &&
                (st.name().equals("PENDING") || st.name().equals("MISSING"));

            if (isCompleted) completed++;
            if (isRemaining) remaining++;

            if (submittedAt != null && due != null) {
                if (!submittedAt.isAfter(due)) onTime++;
                else late++;
            } else if (st != null && st.name().equals("LATE")) {
                late++;
            }
        }

        int total = completed + remaining;
        int progress = total == 0 ? 0 : (int) Math.round((completed * 100.0) / total);

        return ProgressSummary.builder()
            .completedCount(completed)
            .remainingCount(remaining)
            .onTimeCount(onTime)
            .lateCount(late)
            .progressPercent(progress)
            .build();
    }

    @Override
    public ProgressSummary fetchUserEducationProgress(Long userId, Long programId) {

        List<Tuple> rows = query
            .select(training.status, training.completedAt, training.programTraining.endDate)
            .from(training)
            .where(
                training.user.userId.eq(userId),
                training.programTraining.program.programId.eq(programId)
            )
            .fetch();

        int completed = 0;
        int remaining = 0;
        int onTime = 0;
        int late = 0;

        for (Tuple t : rows) {
            var st = t.get(training.status);
            var completedAt = t.get(training.completedAt);
            var endDate = t.get(training.programTraining.endDate); // LocalDate

            boolean isCompleted = st != null && st.name().equals("COMPLETED");
            if (isCompleted) completed++;
            else remaining++;

            if (isCompleted && completedAt != null && endDate != null) {
                var due = endDate.atTime(23, 59, 59);
                if (!completedAt.isAfter(due)) onTime++;
                else late++;
            }
        }

        int total = completed + remaining;
        int progress = total == 0 ? 0 : (int) Math.round((completed * 100.0) / total);

        return ProgressSummary.builder()
            .completedCount(completed)
            .remainingCount(remaining)
            .onTimeCount(onTime)
            .lateCount(late)
            .progressPercent(progress)
            .build();
    }

    @Override
    public BigDecimal fetchUserAvgTaskScore(Long userId, Long programId) {

        Double avg = query
            .select(task.grade.avg())
            .from(task)
            .where(
                task.deleted.isFalse(),
                task.user.userId.eq(userId),
                task.programTask.onboardingProgram.programId.eq(programId),
                task.status.stringValue().eq("GRADED"),
                task.grade.isNotNull()
            )
            .fetchOne();

        return avg == null
            ? BigDecimal.ZERO
            : BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP);
    }

    @Override
    public long fetchUserMentoringCompletedCount(Long userId, Long programId) {

        Long cnt = query
            .select(mentoring.count())
            .from(mentoring)
            .where(
                mentoring.deleted.isFalse(),
                mentoring.program.programId.eq(programId),
                mentoring.mentee.userId.eq(userId),
                mentoring.status.eq(MentoringStatus.COMPLETED)
            )
            .fetchOne();

        return cnt == null ? 0L : cnt;
    }

    // ✅✅✅ 여기 구현이 핵심(원래 0L로 비어있던 부분)
    @Override
    public Long fetchLatestProgramIdForUser(Long userId, Long departmentId) {
        if (userId == null || departmentId == null) return null;

        ProgramCandidate best = null;

        ProgramCandidate c1 = latestFromTask(userId, departmentId);
        best = pickLatest(best, c1);

        ProgramCandidate c2 = latestFromTraining(userId, departmentId);
        best = pickLatest(best, c2);

        ProgramCandidate c3 = latestFromMentoring(userId, departmentId);
        best = pickLatest(best, c3);

        return best == null ? null : best.programId();
    }

    private record ProgramCandidate(Long programId, LocalDate startDate) {}

    private ProgramCandidate pickLatest(ProgramCandidate a, ProgramCandidate b) {
        if (b == null) return a;
        if (a == null) return b;

        if (a.startDate() == null && b.startDate() != null) return b;
        if (a.startDate() != null && b.startDate() == null) return a;

        if (a.startDate() != null) {
            int cmp = b.startDate().compareTo(a.startDate());
            if (cmp > 0) return b;
            if (cmp < 0) return a;
        }

        // startDate 같거나 둘 다 null이면 programId 큰 쪽 우선
        if (a.programId() == null) return b;
        if (b.programId() == null) return a;
        return b.programId() > a.programId() ? b : a;
    }

    private ProgramCandidate latestFromTask(Long userId, Long departmentId) {
        Tuple t = query
            .select(program.programId, program.startDate)
            .from(task)
            .join(task.programTask.onboardingProgram, program)
            .where(
                task.deleted.isFalse(),
                task.user.userId.eq(userId),
                program.deleted.isFalse(),
                program.department.departmentId.eq(departmentId)
            )
            .orderBy(program.startDate.desc().nullsLast(), program.programId.desc())
            .fetchFirst();

        if (t == null) return null;
        return new ProgramCandidate(
            t.get(program.programId),
            t.get(program.startDate)
        );
    }

    private ProgramCandidate latestFromTraining(Long userId, Long departmentId) {
        Tuple t = query
            .select(program.programId, program.startDate)
            .from(training)
            .join(training.programTraining.program, program)
            .where(
                training.user.userId.eq(userId),
                program.deleted.isFalse(),
                program.department.departmentId.eq(departmentId)
            )
            .orderBy(program.startDate.desc().nullsLast(), program.programId.desc())
            .fetchFirst();

        if (t == null) return null;
        return new ProgramCandidate(
            t.get(program.programId),
            t.get(program.startDate)
        );
    }

    private ProgramCandidate latestFromMentoring(Long userId, Long departmentId) {
        Tuple t = query
            .select(program.programId, program.startDate)
            .from(mentoring)
            .join(mentoring.program, program)
            .where(
                mentoring.deleted.isFalse(),
                mentoring.mentee.userId.eq(userId),
                program.deleted.isFalse(),
                program.department.departmentId.eq(departmentId)
            )
            .orderBy(program.startDate.desc().nullsLast(), program.programId.desc())
            .fetchFirst();

        if (t == null) return null;
        return new ProgramCandidate(
            t.get(program.programId),
            t.get(program.startDate)
        );
    }

    // ------------------------ UTIL ------------------------
    private String makePeriodLabel(LocalDate start) {
        return start.getYear() + "년 " + (start.getMonthValue() <= 6 ? "상반기" : "하반기");
    }

    // ------------------------ SUMMARY ------------------------
    private TopSummaryDto fetchTopSummary(
        List<Long> programIds,
        LocalDateTime startDt,
        LocalDateTime endDt,
        LocalDate start,
        LocalDate end,
        BigDecimal overallKpiAvg   // 🔥 외부에서 계산한 값
    ) {

        Long newUsers = query
            .select(user.countDistinct())
            .from(user)
            .where(
                user.isDeleted.isFalse(),
                user.joinedAt.between(startDt, endDt)
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

        Double avgTask = query
            .select(task.grade.avg())
            .from(task)
            .where(
                task.deleted.isFalse(),
                task.programTask.onboardingProgram.programId.in(programIds),
                task.grade.isNotNull()
            )
            .fetchOne();

        return TopSummaryDto.builder()
            .periodLabel(makePeriodLabel(start))
            .startDate(start)
            .endDate(end)
            .newUserCount(Optional.ofNullable(newUsers).orElse(0L))
            .totalMentoringCount(Optional.ofNullable(totalMentoring).orElse(0L))
            .avgKpiScore(overallKpiAvg) // 🔥 여기!
            .avgTaskScore(
                avgTask == null ? BigDecimal.ZERO :
                    BigDecimal.valueOf(avgTask).setScale(1, RoundingMode.HALF_UP)
            )
            .build();
    }

    // ------------------------ DEPARTMENT METRICS ------------------------
    private List<DepartmentDashboardDto> fetchDepartmentStats(
        List<Long> programIds,
        LocalDateTime startDt,
        LocalDateTime endDt
    ) {

        List<Tuple> rows = query
            .select(
                dept.departmentId,
                dept.name,
                goal.kpiGoalId,
                goal.weight,
                goal.targetValue,
                kpi.achievedValue.avg()
            )
            .from(dept)
            .leftJoin(goal).on(
                goal.departmentId.eq(dept.departmentId),
                goal.isDeleted.isFalse()
            )
            .leftJoin(kpi).on(
                kpi.departmentId.eq(dept.departmentId),
                kpi.kpiGoalId.eq(goal.kpiGoalId),
                kpi.isDeleted.isFalse(),
                kpi.evaluatedAt.between(startDt, endDt)
            )
            .groupBy(
                dept.departmentId,
                dept.name,
                goal.kpiGoalId,
                goal.weight,
                goal.targetValue
            )
            .fetch();

        Map<Long, String> deptNameMap = new HashMap<>();
        Map<Long, BigDecimal> weightedCurrentSum = new HashMap<>();
        Map<Long, BigDecimal> weightedTargetSum = new HashMap<>();
        Map<Long, BigDecimal> weightSum = new HashMap<>(); // 🔥 추가

        for (Tuple t : rows) {
            Long deptId = t.get(dept.departmentId);
            deptNameMap.putIfAbsent(deptId, t.get(dept.name));

            BigDecimal weight = nvl(t.get(goal.weight));
            if (weight.compareTo(BigDecimal.ZERO) == 0) continue;

            Double avgAchieved = t.get(kpi.achievedValue.avg());
            BigDecimal achievedAvg = avgAchieved == null
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(avgAchieved);

            Integer targetInt = t.get(goal.targetValue);
            BigDecimal target = targetInt == null
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(targetInt);

            weightedCurrentSum.merge(
                deptId,
                achievedAvg.multiply(weight),
                BigDecimal::add
            );

            weightedTargetSum.merge(
                deptId,
                target.multiply(weight),
                BigDecimal::add
            );

            weightSum.merge(deptId, weight, BigDecimal::add); // 🔥
        }

        Map<Long, Long> mentoringMap = query
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
            .fetch()
            .stream()
            .collect(Collectors.toMap(
                t -> t.get(program.department.departmentId),
                t -> Optional.ofNullable(t.get(mentoring.count())).orElse(0L)
            ));

        return deptNameMap.keySet().stream()
            .map(deptId -> {
                BigDecimal totalWeight = weightSum.getOrDefault(deptId, BigDecimal.ONE);

                BigDecimal currentAvg = weightedCurrentSum.getOrDefault(deptId, BigDecimal.ZERO)
                    .divide(totalWeight, 2, RoundingMode.HALF_UP);

                BigDecimal targetAvg = weightedTargetSum.getOrDefault(deptId, BigDecimal.ZERO)
                    .divide(totalWeight, 2, RoundingMode.HALF_UP);

                return DepartmentDashboardDto.builder()
                    .departmentId(deptId)
                    .departmentName(deptNameMap.get(deptId))
                    .currentAvgKpi(currentAvg)
                    .targetAvgKpi(targetAvg)
                    .mentoringCount(
                        mentoringMap.getOrDefault(deptId, 0L)
                    )
                    .build();
            })
            .toList();
    }

    // ------------------------ RADAR (PERIOD) ------------------------
    private RadarDashboardDto fetchRadar(
        List<Long> departmentIds,
        LocalDateTime startDt,
        LocalDateTime endDt,
        String periodLabel
    ) {

        // 1) 부서별 KPI Goals 모두 불러오기
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

        // 2) KPI Results 가져오기 (✅ 기간+부서 기준으로 필터)
        List<Tuple> resultTuples = query
            .select(
                kpi.departmentId,
                kpi.kpiGoalId,
                kpi.achievedValue
            )
            .from(kpi)
            .where(
                kpi.departmentId.in(departmentIds),
                kpi.isDeleted.isFalse(),
                kpi.evaluatedAt.between(startDt, endDt)
            )
            .fetch();

        Map<Long, Map<Long, List<BigDecimal>>> scoreMap = new HashMap<>();
        for (Tuple t : resultTuples) {
            Long deptId = t.get(kpi.departmentId);
            Long goalId = t.get(kpi.kpiGoalId);
            BigDecimal score = t.get(kpi.achievedValue);

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

        // 4) Radar 구성
        List<RadarDepartmentScoreDto> radarDepartments = new ArrayList<>();

        for (Tuple d : deptRows) {
            Long deptId = d.get(dept.departmentId);
            String deptName = d.get(dept.name);

            List<KpiGoalDto> deptGoals = allGoals.stream()
                .filter(g -> Objects.equals(g.getDepartmentId(), deptId))
                .toList();

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

                    return new RadarPointDto(g.getKpiGoalId(), g.getDescription(), avg);
                })
                .toList();

            radarDepartments.add(new RadarDepartmentScoreDto(deptId, deptName, points));
        }

        return RadarDashboardDto.builder()
            .periodLabel(periodLabel)
            .departments(radarDepartments)
            .build();
    }

    private BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}

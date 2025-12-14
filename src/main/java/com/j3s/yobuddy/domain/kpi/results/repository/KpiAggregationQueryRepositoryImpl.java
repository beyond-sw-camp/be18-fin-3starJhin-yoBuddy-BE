package com.j3s.yobuddy.domain.kpi.results.repository;

import static com.j3s.yobuddy.domain.programenrollment.entity.ProgramEnrollment.EnrollmentStatus.ACTIVE;
import static com.j3s.yobuddy.domain.programenrollment.entity.ProgramEnrollment.EnrollmentStatus.COMPLETED;

import com.j3s.yobuddy.domain.programenrollment.entity.QProgramEnrollment;
import com.j3s.yobuddy.domain.task.entity.QUserTask;
import com.j3s.yobuddy.domain.training.entity.QUserTraining;
import com.j3s.yobuddy.domain.training.entity.TrainingType;
import com.j3s.yobuddy.domain.weeklyReport.entity.QWeeklyReport;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class KpiAggregationQueryRepositoryImpl implements KpiAggregationQueryRepository {

    private final JPAQueryFactory query;

    private final QUserTraining ut = QUserTraining.userTraining;
    private final QUserTask t = QUserTask.userTask;
    private final QWeeklyReport wr = QWeeklyReport.weeklyReport;

    private NumberExpression<Long> sum(NumberExpression<Long> expr) {
        return Expressions.numberTemplate(Long.class, "sum({0})", expr);
    }

    @Override
    public List<Tuple> aggregateTrainingCompletion(Long programId) {

        NumberExpression<Long> completedCase =
            new CaseBuilder()
                .when(ut.status.stringValue().eq("COMPLETED"))
                .then(1L)
                .otherwise(0L);

        return query
            .select(
                ut.user.userId,
                ut.userTrainingId.count(),
                sum(completedCase)
            )
            .from(ut)
            .where(
                ut.programTraining.program.programId.eq(programId)
            )
            .groupBy(ut.user.userId)
            .fetch();
    }

    @Override
    public List<Tuple> aggregateTaskSubmitRate(Long programId) {

        NumberExpression<Long> submittedCase =
            new CaseBuilder()
                .when(t.status.stringValue().in("SUBMITTED", "GRADED", "LATE"))
                .then(1L)
                .otherwise(0L);

        return query
            .select(
                t.user.userId,
                t.id.count(),
                sum(submittedCase)
            )
            .from(t)
            .where(
                t.deleted.isFalse(),
                t.programTask.onboardingProgram.programId.eq(programId)
            )
            .groupBy(t.user.userId)
            .fetch();
    }

    @Override
    public List<Tuple> aggregateAvgTaskScore(Long programId) {
        return query
            .select(
                t.user.userId,
                t.grade.avg()
            )
            .from(t)
            .where(
                t.deleted.isFalse(),
                t.programTask.onboardingProgram.programId.eq(programId),
                t.status.stringValue().eq("GRADED"),
                t.grade.isNotNull()
            )
            .groupBy(t.user.userId)
            .fetch();
    }

    @Override
    public List<Tuple> aggregateOfflineAttendance(Long programId) {

        NumberExpression<Long> completedCase =
            new CaseBuilder()
                .when(ut.status.stringValue().eq("COMPLETED"))
                .then(1L)
                .otherwise(0L);

        return query
            .select(
                ut.user.userId,
                ut.userTrainingId.count(),
                sum(completedCase)
            )
            .from(ut)
            .where(
                ut.programTraining.program.programId.eq(programId),
                ut.programTraining.training.type.eq(TrainingType.OFFLINE)
            )
            .groupBy(ut.user.userId)
            .fetch();
    }

    @Override
    public List<Tuple> aggregateWeeklyReportByPeriod(
        Long programId,
        LocalDateTime startDt,
        LocalDateTime endDt
    ) {
        QProgramEnrollment pe = QProgramEnrollment.programEnrollment;

        return query
            .select(
                wr.menteeId,                     // 0
                wr.weekNumber.countDistinct(),   // 1 제출 주차 수
                wr.weekNumber.max()              // 2 최대 주차 (만점)
            )
            .from(pe)
            .join(wr).on(
                wr.menteeId.eq(pe.user.userId),
                wr.createdAt.between(startDt, endDt),
                wr.status.stringValue().in("SUBMITTED", "REVIEWED")
            )
            .where(
                pe.program.programId.eq(programId),
                pe.status.in(ACTIVE, COMPLETED)
            )
            .groupBy(wr.menteeId)
            .fetch();
    }
}

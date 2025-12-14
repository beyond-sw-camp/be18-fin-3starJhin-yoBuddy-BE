package com.j3s.yobuddy.domain.mentor.dashboard.repository;

import com.j3s.yobuddy.domain.kpi.results.entity.QKpiResults;
import com.j3s.yobuddy.domain.mentor.dashboard.response.MenteeOnboardingPerformanceResponse;
import com.j3s.yobuddy.domain.mentor.dashboard.response.MenteeOnboardingPerformanceResponse.Header;
import com.j3s.yobuddy.domain.mentor.dashboard.response.MenteeOnboardingPerformanceResponse.TaskSection;
import com.j3s.yobuddy.domain.mentor.dashboard.response.MenteeOnboardingPerformanceResponse.TrainingSection;
import com.j3s.yobuddy.domain.mentor.dashboard.response.MenteeOnboardingPerformanceResponse.WeeklyReportItem;
import com.j3s.yobuddy.domain.mentor.mentoring.entity.QMentoringSession;
import com.j3s.yobuddy.domain.task.entity.QUserTask;
import com.j3s.yobuddy.domain.task.entity.UserTaskStatus;
import com.j3s.yobuddy.domain.training.entity.QUserTraining;
import com.j3s.yobuddy.domain.training.entity.UserTrainingStatus;
import com.j3s.yobuddy.domain.user.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MenteePerformanceQueryRepositoryImpl implements MenteePerformanceQueryRepository {

    private final JPAQueryFactory query;

    private final QMentoringSession ms = QMentoringSession.mentoringSession;
    private final QUser user = QUser.user;
    private final QUserTraining ut = QUserTraining.userTraining;
    private final QUserTask utask = QUserTask.userTask;
    private final QKpiResults kpi = QKpiResults.kpiResults;

    @Override
    public MenteeOnboardingPerformanceResponse getMenteeOnboardingPerformance(
        Long mentorId,
        Long menteeId,
        LocalDate from,
        LocalDate to
    ) {
        // [공통 기간] from 00:00 ~ to 다음날 00:00
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt = to.plusDays(1).atStartOfDay();

        // 1) 멘티 기본 정보
        String menteeName = query
            .select(user.name)
            .from(user)
            .where(user.userId.eq(menteeId))
            .fetchOne();

        // 2) 멘토링 횟수 (멘토+멘티+기간)
        Long mentoringCount = query
            .select(ms.count())
            .from(ms)
            .where(
                ms.mentor.userId.eq(mentorId),
                ms.mentee.userId.eq(menteeId),
                ms.scheduledAt.between(fromDt, toDt)
            )
            .fetchOne();
        if (mentoringCount == null) mentoringCount = 0L;

        // 3) 교육 이수 현황 (UserTraining)
        Long totalTrainings = query
            .select(ut.count())
            .from(ut)
            .where(ut.user.userId.eq(menteeId))
            .fetchOne();
        if (totalTrainings == null) totalTrainings = 0L;

        Long completedTrainings = query
            .select(ut.count())
            .from(ut)
            .where(
                ut.user.userId.eq(menteeId),
                ut.status.eq(UserTrainingStatus.COMPLETED)
            )
            .fetchOne();
        if (completedTrainings == null) completedTrainings = 0L;

        long remainingTrainings = totalTrainings - completedTrainings;
        double completionRate = totalTrainings > 0
            ? (completedTrainings * 100.0) / totalTrainings
            : 0.0;

        // 4) 과제(Task) 현황 (UserTask)
        Long totalTasks = query
            .select(utask.count())
            .from(utask)
            .where(
                utask.user.userId.eq(menteeId),
                utask.deleted.isFalse(),
                utask.createdAt.between(fromDt, toDt)
            )
            .fetchOne();
        if (totalTasks == null) totalTasks = 0L;

        Long submittedTasks = query
            .select(utask.count())
            .from(utask)
            .where(
                utask.user.userId.eq(menteeId),
                utask.deleted.isFalse(),
                utask.createdAt.between(fromDt, toDt),
                utask.status.in(
                    UserTaskStatus.SUBMITTED,
                    UserTaskStatus.GRADED,
                    UserTaskStatus.LATE
                )
            )
            .fetchOne();
        if (submittedTasks == null) submittedTasks = 0L;

        Long lateTasks = query
            .select(utask.count())
            .from(utask)
            .where(
                utask.user.userId.eq(menteeId),
                utask.deleted.isFalse(),
                utask.createdAt.between(fromDt, toDt),
                utask.status.eq(UserTaskStatus.LATE)
            )
            .fetchOne();
        if (lateTasks == null) lateTasks = 0L;

        long remainingTasks = totalTasks - submittedTasks;
        if (remainingTasks < 0) remainingTasks = 0;

        long onTimeSubmitted = submittedTasks - lateTasks;
        if (onTimeSubmitted < 0) onTimeSubmitted = 0;

        double submissionRate = totalTasks > 0
            ? (submittedTasks * 100.0) / totalTasks
            : 0.0;

        double onTimeRate = submittedTasks > 0
            ? (onTimeSubmitted * 100.0) / submittedTasks
            : 0.0;

        double lateRate = submittedTasks > 0
            ? (lateTasks * 100.0) / submittedTasks
            : 0.0;

        // 5) 평균 과제 점수
        Double avgTaskScore = query
            .select(utask.grade.avg())
            .from(utask)
            .where(
                utask.user.userId.eq(menteeId),
                utask.deleted.isFalse(),
                utask.grade.isNotNull(),
                utask.createdAt.between(fromDt, toDt)
            )
            .fetchOne();

        // 6) KPI 평균 점수 (kpi_results.score 평균)
        Double avgKpiScore = query
            .select(kpi.achievedValue.avg())
            .from(kpi)
            .where(
                kpi.userId.eq(menteeId),
                kpi.isDeleted.isFalse(),
                kpi.createdAt.between(fromDt, toDt)
            )
            .fetchOne();

        // === Header ===
        Header header = Header.builder()
            .menteeId(menteeId)
            .menteeName(menteeName)
            .periodStart(from)
            .periodEnd(to)
            .mentoringCount(mentoringCount.intValue())
            .totalMentoringHours(0.0) // TODO: 세션 길이 생기면 실제 시간 계산
            .averageTaskScore(avgTaskScore) // null 허용
            .kpiScore(avgKpiScore)          // null 허용
            .build();

        // === TaskSection ===
        TaskSection taskSection = TaskSection.builder()
            .totalTasks(totalTasks.intValue())
            .submittedTasks(submittedTasks.intValue())
            .remainingTasks((int) remainingTasks)
            .submissionRate(submissionRate)
            .onTimeRate(onTimeRate)
            .lateRate(lateRate)
            .build();

        // === TrainingSection ===
        TrainingSection trainingSection = TrainingSection.builder()
            .totalTrainings(totalTrainings.intValue())
            .completedTrainings(completedTrainings.intValue())
            .remainingTrainings((int) remainingTrainings)
            .completionRate(completionRate)
            .build();

        // === WeeklyReports: 아직 엔티티 정보 없으니 빈 리스트 ===
        List<WeeklyReportItem> weeklyReports = Collections.emptyList();

        return MenteeOnboardingPerformanceResponse.builder()
            .header(header)
            .taskSection(taskSection)
            .trainingSection(trainingSection)
            .weeklyReports(weeklyReports)
            .build();
    }
}

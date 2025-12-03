package com.j3s.yobuddy.domain.user.dashboard.repository;

import com.j3s.yobuddy.domain.mentor.mentoring.entity.MentoringStatus;
import com.j3s.yobuddy.domain.mentor.mentoring.entity.QMentoringSession;
import com.j3s.yobuddy.domain.task.entity.QUserTask;
import com.j3s.yobuddy.domain.task.entity.UserTaskStatus;
import com.j3s.yobuddy.domain.training.entity.QUserTraining;
import com.j3s.yobuddy.domain.training.entity.TrainingType;
import com.j3s.yobuddy.domain.training.entity.UserTrainingStatus;
import com.j3s.yobuddy.domain.user.dashboard.response.UserOnboardingPerformanceResponse;
import com.j3s.yobuddy.domain.user.dashboard.response.UserOnboardingPerformanceResponse.Header;
import com.j3s.yobuddy.domain.user.dashboard.response.UserOnboardingPerformanceResponse.MentoringSection;
import com.j3s.yobuddy.domain.user.dashboard.response.UserOnboardingPerformanceResponse.TaskSection;
import com.j3s.yobuddy.domain.user.dashboard.response.UserOnboardingPerformanceResponse.TrainingSection;
import com.j3s.yobuddy.domain.user.dashboard.response.UserOnboardingPerformanceResponse.WeeklyReportItem;
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
public class UserPerformanceQueryRepositoryImpl implements UserPerformanceQueryRepository {

    private final JPAQueryFactory query;

    private final QMentoringSession ms = QMentoringSession.mentoringSession;
    private final QUser user = QUser.user;
    private final QUserTraining ut1 = QUserTraining.userTraining;
    private final QUserTask ut2 = QUserTask.userTask;


    @Override
    public UserOnboardingPerformanceResponse getOnboardingPerformance(Long userId, Long mentorId,
        LocalDate from, LocalDate to) {

        LocalDateTime fromDate = from.atStartOfDay();
        LocalDateTime toDate = to.plusDays(1).atStartOfDay();

        // 1) 멘토 기본 정보
        String mentorName = query
            .select(user.name)
            .from(user)
            .where(user.userId.eq(mentorId))
            .fetchOne();

        // 2) 멘토링 횟수 (멘토+멘티+기간)
        Long mentoringCount = query
            .select(ms.count())
            .from(ms)
            .where(
                ms.mentor.userId.eq(mentorId),
                ms.mentee.userId.eq(userId),
                ms.scheduledAt.between(fromDate, toDate)
            )
            .fetchOne();
        if (mentoringCount == null) {
            mentoringCount = 0L;
        }

        Long totalMentoring = query
            .select(ms.count())
            .from(ms)
            .where(
                ms.mentor.userId.eq(mentorId),
                ms.mentee.userId.eq(userId)
            )
            .fetchOne();
        if (totalMentoring == null) {
            totalMentoring = 0L;
        }

        Long completedMentoring = query
            .select(ms.count())
            .from(ms)
            .where(ms.mentee.userId.eq(userId),
                ms.mentor.userId.eq(mentorId),
                ms.status.eq(MentoringStatus.COMPLETED))
            .fetchOne();
        if (completedMentoring == null) {
            completedMentoring = 0L;
        }

        Long notCompletedMentoring = query
            .select(ms.count())
            .from(ms)
            .where(ms.mentee.userId.eq(userId),
                ms.mentor.userId.eq(mentorId),
                ms.status.eq(MentoringStatus.SCHEDULED))
            .fetchOne();
        if (notCompletedMentoring == null) {
            notCompletedMentoring = 0L;
        }

        Long noShowMentoring = query
            .select(ms.count())
            .from(ms)
            .where(ms.mentee.userId.eq(userId),
                ms.mentor.userId.eq(mentorId),
                ms.status.eq(MentoringStatus.NO_SHOW))
            .fetchOne();
        if (noShowMentoring == null) {
            noShowMentoring = 0L;
        }

        double completedMentoringRate = totalMentoring > 0
            ? (completedMentoring * 100.0) / totalMentoring
            : 0.0;

        long finishedMentoring = completedMentoring + noShowMentoring;

        double onTimeMentoringRate = finishedMentoring > 0
            ? (completedMentoring * 100.0) / finishedMentoring
            : 0.0;

        // 3) 교육 이수 현황 (UserTraining)
        Long totalTrainings = query
            .select(ut1.count())
            .from(ut1)
            .where(ut1.user.userId.eq(userId))
            .fetchOne();
        if (totalTrainings == null) {
            totalTrainings = 0L;
        }

        Long completedTrainings = query
            .select(ut1.count())
            .from(ut1)
            .where(
                ut1.user.userId.eq(userId),
                ut1.status.eq(UserTrainingStatus.COMPLETED)
            )
            .fetchOne();
        if (completedTrainings == null) {
            completedTrainings = 0L;
        }

        long remainingTrainings = totalTrainings - completedTrainings;
        double completionRate = totalTrainings > 0
            ? (completedTrainings * 100.0) / totalTrainings
            : 0.0;
        Double avgTrainingScore = query
            .select(ut1.score.avg())
            .from(ut1)
            .where(
                ut1.user.userId.eq(userId),
                ut1.programTraining.training.type.eq(TrainingType.OFFLINE),
                ut1.score.isNotNull(),
                ut1.createdAt.between(fromDate, toDate)
            )
            .fetchOne();

        // 4) 과제(Task) 현황 (UserTask)
        Long totalTasks = query
            .select(ut2.count())
            .from(ut2)
            .where(
                ut2.user.userId.eq(userId),
                ut2.deleted.isFalse(),
                ut2.createdAt.between(fromDate, toDate)
            )
            .fetchOne();
        if (totalTasks == null) {
            totalTasks = 0L;
        }

        Long submittedTasks = query
            .select(ut2.count())
            .from(ut2)
            .where(
                ut2.user.userId.eq(userId),
                ut2.deleted.isFalse(),
                ut2.createdAt.between(fromDate, toDate),
                ut2.status.in(
                    UserTaskStatus.SUBMITTED,
                    UserTaskStatus.GRADED,
                    UserTaskStatus.LATE
                )
            )
            .fetchOne();
        if (submittedTasks == null) {
            submittedTasks = 0L;
        }

        Long lateTasks = query
            .select(ut2.count())
            .from(ut2)
            .where(
                ut2.user.userId.eq(userId),
                ut2.deleted.isFalse(),
                ut2.createdAt.between(fromDate, toDate),
                ut2.status.eq(UserTaskStatus.LATE)
            )
            .fetchOne();
        if (lateTasks == null) {
            lateTasks = 0L;
        }

        long remainingTasks = totalTasks - submittedTasks;
        if (remainingTasks < 0) {
            remainingTasks = 0;
        }

        long onTimeSubmitted = submittedTasks - lateTasks;
        if (onTimeSubmitted < 0) {
            onTimeSubmitted = 0;
        }

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
            .select(ut2.grade.avg())
            .from(ut2)
            .where(
                ut2.user.userId.eq(userId),
                ut2.deleted.isFalse(),
                ut2.grade.isNotNull(),
                ut2.createdAt.between(fromDate, toDate)
            )
            .fetchOne();
        Header header = Header.builder()
            .mentorId(mentorId)
            .mentorName(mentorName)
            .periodStart(from)
            .periodEnd(to)
            .mentoringCount(mentoringCount.intValue())
            .totalMentoringHours(0.0) // TODO: 세션 길이 생기면 실제 시간 계산
            .averageTaskScore(avgTaskScore) // null 허용
            .averageTrainingScore(avgTrainingScore)
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

        MentoringSection mentoringSection = MentoringSection.builder()
            .totalMentoring(totalMentoring.intValue())
            .completedMentoring(completedMentoring.intValue())
            .notCompletedMentoring(notCompletedMentoring.intValue())
            .noShowMentoring(noShowMentoring.intValue())
            .completedMentoringRate(completedMentoringRate)
            .onTimeMentoringRate(onTimeMentoringRate)
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

        return UserOnboardingPerformanceResponse.builder()
            .header(header)
            .mentoringSection(mentoringSection)
            .taskSection(taskSection)
            .trainingSection(trainingSection)
            .weeklyReports(weeklyReports)
            .build();
    }
}

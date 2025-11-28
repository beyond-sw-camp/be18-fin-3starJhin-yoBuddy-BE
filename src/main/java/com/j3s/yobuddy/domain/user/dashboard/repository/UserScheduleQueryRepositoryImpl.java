package com.j3s.yobuddy.domain.user.dashboard.repository;

import com.j3s.yobuddy.domain.mentor.mentoring.entity.QMentoringSession;
import com.j3s.yobuddy.domain.task.entity.QUserTask;
import com.j3s.yobuddy.domain.training.entity.QUserTraining;
import com.j3s.yobuddy.domain.training.entity.TrainingType;
import com.j3s.yobuddy.domain.user.dashboard.response.UserScheduleResponse;
import com.j3s.yobuddy.domain.user.dashboard.response.UserScheduleResponse.ScheduleItem;
import com.j3s.yobuddy.domain.user.dashboard.response.UserScheduleResponse.ScheduleType;
import com.j3s.yobuddy.domain.user.entity.QUser;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserScheduleQueryRepositoryImpl implements UserScheduleQueryRepository {

    private final JPAQueryFactory query;

    private final QMentoringSession ms = QMentoringSession.mentoringSession;
    private final QUserTraining ut1 = QUserTraining.userTraining;
    private final QUserTask ut2 = QUserTask.userTask;
    private final QUser user = QUser.user;

    @Override
    public UserScheduleResponse getMonthlySchedule(Long userId, YearMonth month) {

        List<ScheduleItem> items = new ArrayList<>();

        List<Tuple> mentoringRows = query
            .select(
                ms.id,
                ms.scheduledAt,
                ms.mentor.name,
                ms.status
            )
            .from(ms)
            .where(
                ms.mentee.userId.eq(userId),
                ms.scheduledAt.year().eq(month.getYear()),
                ms.scheduledAt.month().eq(month.getMonthValue())
            )
            .orderBy(ms.scheduledAt.asc())
            .fetch();

        for (Tuple t : mentoringRows) {
            LocalDateTime at = t.get(ms.scheduledAt);

            items.add(new UserScheduleResponse.ScheduleItem(
                ScheduleType.MENTORING,
                t.get(ms.id),                         // sessionId
                null,                                 // userTaskId
                null,                                 // userTrainingId
                at.toLocalDate().toString(),          // date
                at.toLocalTime().toString(),          // time
                t.get(ms.mentor.name),                // mentorName
                t.get(ms.status).name()               // status
            ));
        }

        List<Tuple> trainingRows = query
            .select(
                ut1.userTrainingId,
                ut1.programTraining.scheduledAt,   // ← 실제 필드명으로 변경
                ut1.status
            )
            .from(ut1)
            .where(
                ut1.user.userId.eq(userId),
                ut1.programTraining.scheduledAt.year().eq(month.getYear()),     // ← 필드명 맞게 수정
                ut1.programTraining.scheduledAt.month().eq(month.getMonthValue())
            )
            .fetch();

        for (Tuple t : trainingRows) {
            LocalDateTime at = t.get(ut1.programTraining.scheduledAt);          // ← 필드명 맞게 수정

            items.add(new UserScheduleResponse.ScheduleItem(
                ScheduleType.TRAINING,
                null,                                 // sessionId
                null,                                 // userTaskId
                t.get(ut1.userTrainingId),                        // userTrainingId
                at.toLocalDate().toString(),          // date
                at.toLocalTime().toString(),          // time
                null,                                 // mentorName (교육이면 보통 없음)
                t.get(ut1.status).name()              // status
            ));
        }

        List<Tuple> taskRows = query
            .select(
                ut2.id,
                ut2.programTask.dueDate,        // ← 실제 필드명으로 변경
                ut2.status
            )
            .from(ut2)
            .where(
                ut2.user.userId.eq(userId),
                ut2.programTask.dueDate.year().eq(month.getYear()),           // ← 필드명 맞게 수정
                ut2.programTask.dueDate.month().eq(month.getMonthValue())
            )
            .fetch();

        for (Tuple t : taskRows) {
            LocalDateTime at = t.get(ut2.programTask.dueDate);                // ← 필드명 맞게 수정

            items.add(new UserScheduleResponse.ScheduleItem(
                ScheduleType.TASK,
                null,                                 // sessionId
                t.get(ut2.id),                        // userTaskId
                null,                                 // userTrainingId
                at.toLocalDate().toString(),          // date
                at.toLocalTime().toString(),          // time
                null,                                 // mentorName (과제는 보통 없음)
                t.get(ut2.status).name()              // status
            ));
        }
        items.sort(Comparator
            .comparing(UserScheduleResponse.ScheduleItem::getDate)
            .thenComparing(UserScheduleResponse.ScheduleItem::getTime));

        return new UserScheduleResponse(items);
    }

    @Override
    public UserScheduleResponse getWeeklySchedule(Long userId, LocalDate start, LocalDate end) {

        List<ScheduleItem> items = new ArrayList<>();

        List<Tuple> tasks = query
            .select(
                ut2.id,
                ut2.programTask.dueDate,
                ut2.status
            )
            .from(ut2)
            .where(
                ut2.user.userId.eq(userId),
                ut2.programTask.dueDate.between(start.atStartOfDay(), end.atTime(23, 59))
            )
            .fetch();

        for (Tuple t : tasks) {
            LocalDateTime at = t.get(ut2.programTask.dueDate);

            items.add(new ScheduleItem(
                ScheduleType.TASK,
                null,
                t.get(ut2.id),
                null,
                at.toLocalDate().toString(),
                at.toLocalTime().toString(),
                null,   // mentorName 없음
                t.get(ut2.status).name()
            ));
        }

        List<Tuple> trainings = query
            .select(
                ut1.userTrainingId,
                ut1.programTraining.scheduledAt,   // 실제 필드명 사용
                ut1.status
            )
            .from(ut1)
            .where(
                ut1.user.userId.eq(userId),
                ut1.programTraining.scheduledAt.between(start.atStartOfDay(), end.atTime(23, 59)),
                ut1.programTraining.training.type.eq(TrainingType.ONLINE)
            )
            .fetch();
        for (Tuple t : trainings) {
            LocalDateTime at = t.get(ut1.programTraining.scheduledAt);

            items.add(new ScheduleItem(
                ScheduleType.TRAINING,
                null,
                null,
                t.get(ut1.userTrainingId),
                at.toLocalDate().toString(),
                at.toLocalTime().toString(),
                null,
                t.get(ut1.status).name()
            ));
        }

        items.sort(Comparator
            .comparing(ScheduleItem::getDate)
            .thenComparing(ScheduleItem::getTime)
        );

        return new UserScheduleResponse(items);
    }
}

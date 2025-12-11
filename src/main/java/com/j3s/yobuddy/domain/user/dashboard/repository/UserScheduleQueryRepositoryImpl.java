package com.j3s.yobuddy.domain.user.dashboard.repository;

import com.j3s.yobuddy.domain.mentor.mentoring.entity.QMentoringSession;
import com.j3s.yobuddy.domain.task.entity.QUserTask;
import com.j3s.yobuddy.domain.training.entity.QUserTraining;
import com.j3s.yobuddy.domain.user.dashboard.response.UserScheduleResponse;
import com.j3s.yobuddy.domain.user.dashboard.response.UserScheduleResponse.ScheduleItem;
import com.j3s.yobuddy.domain.user.dashboard.response.UserScheduleResponse.ScheduleType;
import com.j3s.yobuddy.domain.user.entity.QUser;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
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

        List<Tuple> taskRows = query
            .select(
                ut2.id,
                ut2.programTask.dueDate,
                ut2.status,
                ut2.programTask.onboardingTask.title   // 과제 제목
            )
            .from(ut2)
            .where(
                ut2.user.userId.eq(userId),
                ut2.programTask.dueDate.year().eq(month.getYear()),
                ut2.programTask.dueDate.month().eq(month.getMonthValue())
            )
            .fetch();

        for (Tuple t : taskRows) {
            LocalDateTime at = t.get(ut2.programTask.dueDate);

            items.add(new ScheduleItem(
                ScheduleType.TASK,
                null,                       // sessionId
                t.get(ut2.id),              // userTaskId
                null,                       // userTrainingId
                null,                       // trainingId
                at.toLocalDate().toString(),
                at.toLocalTime().toString(),
                null,                       // mentorName
                t.get(ut2.status).name(),
                t.get(ut2.programTask.onboardingTask.title),  // taskTitle
                null                        // trainingTitle
            ));
        }

        LocalDate monthStart = month.atDay(1);
        LocalDate monthEnd = month.atEndOfMonth();

        // OFFLINE: scheduled_at 존재
        BooleanExpression offline =
            ut1.programTraining.scheduledAt.isNotNull()
                .and(ut1.programTraining.scheduledAt.between(
                    monthStart.atStartOfDay(), monthEnd.atTime(23, 59)
                ));

        // ONLINE: 기간(startDate ~ endDate) 존재
        BooleanExpression online =
            ut1.programTraining.scheduledAt.isNull()
                .and(ut1.programTraining.startDate.isNotNull())
                .and(ut1.programTraining.endDate.isNotNull())
                .and(
                    ut1.programTraining.startDate.loe(monthEnd)
                        .and(ut1.programTraining.endDate.goe(monthStart))
                );

        List<Tuple> trainingRows = query
            .select(
                ut1.userTrainingId,
                ut1.programTraining.scheduledAt,
                ut1.programTraining.startDate,
                ut1.programTraining.endDate,
                ut1.status,
                ut1.programTraining.training.trainingId,
                ut1.programTraining.training.title
            )
            .from(ut1)
            .where(
                ut1.user.userId.eq(userId),
                offline.or(online)
            )
            .fetch();

        for (Tuple t : trainingRows) {

            LocalDateTime scheduled = t.get(ut1.programTraining.scheduledAt);
            LocalDate startDate = t.get(ut1.programTraining.startDate);

            String date;
            String time;

            // OFFLINE = scheduled_at
            if (scheduled != null) {
                date = scheduled.toLocalDate().toString();
                time = scheduled.toLocalTime().toString();
            }
            // ONLINE = start_date
            else {
                date = startDate.toString();
                time = null;
            }

            items.add(new ScheduleItem(
                ScheduleType.TRAINING,
                null,
                null,
                t.get(ut1.userTrainingId),
                t.get(ut1.programTraining.training.trainingId),
                date,
                time,
                null,
                t.get(ut1.status).name(),
                null,                                   // taskTitle 없음
                t.get(ut1.programTraining.training.title)  // trainingTitle
            ));
        }

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

            items.add(new ScheduleItem(
                ScheduleType.MENTORING,
                t.get(ms.id),
                null,
                null,
                null,
                at.toLocalDate().toString(),
                at.toLocalTime().toString(),
                t.get(ms.mentor.name),
                t.get(ms.status).name(),
                null,
                null
            ));
        }

        // 정렬
        items.sort(
            Comparator.comparing(ScheduleItem::getDate)
                .thenComparing(ScheduleItem::getTime, Comparator.nullsLast(String::compareTo))
        );

        return new UserScheduleResponse(items);
    }

    @Override
    public UserScheduleResponse getWeeklySchedule(Long userId, LocalDate start, LocalDate end) {

        List<ScheduleItem> items = new ArrayList<>();

        List<Tuple> tasks = query
            .select(
                ut2.id,
                ut2.programTask.dueDate,
                ut2.status,
                ut2.programTask.onboardingTask.title
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
                null,
                at.toLocalDate().toString(),
                at.toLocalTime().toString(),
                null,
                t.get(ut2.status).name(),
                t.get(ut2.programTask.onboardingTask.title),
                null
            ));
        }

        BooleanExpression weeklyOffline =
            ut1.programTraining.scheduledAt.isNotNull()
                .and(ut1.programTraining.scheduledAt.between(
                    start.atStartOfDay(), end.atTime(23, 59)
                ));

        BooleanExpression weeklyOnline =
            ut1.programTraining.scheduledAt.isNull()
                .and(ut1.programTraining.startDate.isNotNull())
                .and(ut1.programTraining.endDate.isNotNull())
                .and(
                    ut1.programTraining.startDate.loe(end)
                        .and(ut1.programTraining.endDate.goe(start))
                );

        List<Tuple> trainings = query
            .select(
                ut1.userTrainingId,
                ut1.programTraining.scheduledAt,
                ut1.programTraining.startDate,
                ut1.programTraining.endDate,
                ut1.status,
                ut1.programTraining.training.trainingId,
                ut1.programTraining.training.title
            )
            .from(ut1)
            .where(
                ut1.user.userId.eq(userId),
                weeklyOffline.or(weeklyOnline)
            )
            .fetch();

        for (Tuple t : trainings) {

            LocalDateTime scheduled = t.get(ut1.programTraining.scheduledAt);
            LocalDate startDate = t.get(ut1.programTraining.startDate);

            String date;
            String time;

            if (scheduled != null) {
                date = scheduled.toLocalDate().toString();
                time = scheduled.toLocalTime().toString();
            } else {
                date = startDate.toString();
                time = null;
            }

            items.add(new ScheduleItem(
                ScheduleType.TRAINING,
                null,
                null,
                t.get(ut1.userTrainingId),
                t.get(ut1.programTraining.training.trainingId),
                date,
                time,
                null,
                t.get(ut1.status).name(),
                null,
                t.get(ut1.programTraining.training.title)
            ));
        }

        items.sort(
            Comparator.comparing(ScheduleItem::getDate)
                .thenComparing(ScheduleItem::getTime, Comparator.nullsLast(String::compareTo))
        );

        return new UserScheduleResponse(items);
    }
}

package com.j3s.yobuddy.domain.mentor.dashboard.repository;

import com.j3s.yobuddy.domain.mentor.dashboard.response.ScheduleResponse;
import com.j3s.yobuddy.domain.mentor.dashboard.response.ScheduleResponse.ScheduleItem;
import com.j3s.yobuddy.domain.mentor.mentoring.entity.QMentoringSession;
import com.j3s.yobuddy.domain.user.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ScheduleQueryRepositoryImpl implements ScheduleQueryRepository {

    private final JPAQueryFactory query;

    private final QMentoringSession ms = QMentoringSession.mentoringSession;
    private final QUser user = QUser.user;

    @Override
    public ScheduleResponse getMonthlySchedule(Long mentorId, YearMonth ym) {

        var rows = query
            .select(
                ms.id,
                ms.scheduledAt,
                user.name,
                ms.status
            )
            .from(ms)
            .join(ms.mentee, user)
            .where(
                ms.mentor.userId.eq(mentorId),
                ms.scheduledAt.year().eq(ym.getYear()),
                ms.scheduledAt.month().eq(ym.getMonthValue())
            )
            .orderBy(ms.scheduledAt.asc())
            .fetch();

        List<ScheduleItem> items = rows.stream()
            .map(tr -> new ScheduleResponse.ScheduleItem(
                tr.get(ms.id),
                tr.get(ms.scheduledAt).toLocalDate().toString(),
                tr.get(ms.scheduledAt).toLocalTime().toString(),
                tr.get(user.name),
                tr.get(ms.status).name()
            ))
            .toList();

        return new ScheduleResponse(items);
    }
}

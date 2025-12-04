package com.j3s.yobuddy.domain.weeklyReport.repository;

import static com.j3s.yobuddy.domain.weeklyReport.entity.QWeeklyReport.weeklyReport;

import com.j3s.yobuddy.domain.weeklyReport.entity.QWeeklyReport;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport.WeeklyReportStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WeeklyReportQueryRepositoryImpl implements WeeklyReportQueryRepository {

    private final JPAQueryFactory query;
    private final QWeeklyReport wr = QWeeklyReport.weeklyReport;

    public Page<WeeklyReport> findWeeklyReports(Long menteeId,
        WeeklyReportStatus status,
        Pageable pageable) {

        List<WeeklyReport> content = query
            .selectFrom(weeklyReport)
            .where(
                weeklyReport.menteeId.eq(menteeId),
                statusEq(status)
            )
            .orderBy(weeklyReport.weekNumber.desc(), weeklyReport.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = query
            .select(weeklyReport.count())
            .from(weeklyReport)
            .where(
                weeklyReport.menteeId.eq(menteeId),
                statusEq(status)
            )
            .fetchOne();

        if (total == null) {
            total = 0L;
        }

        return new PageImpl<>(content, pageable, total);
    }

    public List<WeeklyReport> findDraftReportsEndedBefore(LocalDate date) {
        return query
            .selectFrom(weeklyReport)
            .where(
                weeklyReport.status.eq(WeeklyReportStatus.DRAFT),
                weeklyReport.endDate.before(date)
            )
            .fetch();
    }

    public BooleanExpression statusEq(WeeklyReportStatus status) {
        if (status == null) {
            return null;
        }
        return weeklyReport.status.eq(status);
    }

    @Override
    public Page<WeeklyReport> findWeeklyReportsForMentor(Long mentorId,
        Long menteeId,
        WeeklyReportStatus status,
        Integer weekNumber,
        Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder()
            .and(wr.mentorId.eq(mentorId))
            .and(wr.menteeId.eq(menteeId));

        if (status != null) {
            builder.and(wr.status.eq(status));
        }

        if (weekNumber != null) {
            builder.and(wr.weekNumber.eq(weekNumber));
        }

        JPAQuery<WeeklyReport> jpaQuery = query
            .selectFrom(wr)
            .where(builder)
            .orderBy(wr.weekNumber.desc(), wr.createdAt.desc());

        List<WeeklyReport> content = jpaQuery
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = query
            .select(wr.count())
            .from(wr)
            .where(builder)
            .fetchOne();

        long totalCount = total != null ? total : 0L;

        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public List<WeeklyReport> findSubmittedReportsWithoutFeedbackBefore(LocalDate thresholdDate) {
        return query
            .selectFrom(wr)
            .where(
                wr.status.eq(WeeklyReportStatus.SUBMITTED)
                    .and(wr.mentorFeedback.isNull())
                    .and(wr.endDate.before(thresholdDate))
            )
            .fetch();
    }

    @Override
    public List<WeeklyReport> findSubmittedReportsWithoutFeedbackBetween(LocalDate startInclusive,
        LocalDate endExclusive) {
        return query
            .selectFrom(wr)
            .where(
                wr.status.eq(WeeklyReportStatus.SUBMITTED)
                    .and(wr.mentorFeedback.isNull())
                    .and(wr.endDate.goe(startInclusive))
                    .and(wr.endDate.before(endExclusive))
            )
            .fetch();
    }
}

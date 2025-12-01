package com.j3s.yobuddy.domain.weeklyReport.repository;

import static com.j3s.yobuddy.domain.weeklyReport.entity.QWeeklyReport.weeklyReport;

import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport.WeeklyReportStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
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

    /**
     * 멘티 기준 주간 리포트 목록 조회 (상태 optional)
     */
    public Page<WeeklyReport> findWeeklyReports(Long menteeId,
        WeeklyReportStatus status,
        Pageable pageable) {

        // content query
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

        // count query
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

    /**
     * 마감일이 지났는데 아직 DRAFT인 리포트 조회 (OVERDUE 처리용)
     */
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
}

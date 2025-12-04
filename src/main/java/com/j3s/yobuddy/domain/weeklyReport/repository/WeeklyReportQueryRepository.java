package com.j3s.yobuddy.domain.weeklyReport.repository;

import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport.WeeklyReportStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport;
import org.springframework.stereotype.Repository;

@Repository
public interface WeeklyReportQueryRepository {

    Page<WeeklyReport> findWeeklyReports(Long menteeId,
        WeeklyReportStatus status,
        Pageable pageable);

    List<WeeklyReport> findDraftReportsEndedBefore(LocalDate date);

    BooleanExpression statusEq(WeeklyReportStatus status);

    Page<WeeklyReport> findWeeklyReportsForMentor(Long mentorId,
        Long menteeId,
        WeeklyReportStatus status,
        Integer weekNumber,
        Pageable pageable);

    List<WeeklyReport> findSubmittedReportsWithoutFeedbackBefore(LocalDate thresholdDate);

    List<WeeklyReport> findSubmittedReportsWithoutFeedbackBetween(LocalDate startInclusive, LocalDate endExclusive);
}

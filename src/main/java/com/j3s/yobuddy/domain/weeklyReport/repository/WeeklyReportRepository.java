package com.j3s.yobuddy.domain.weeklyReport.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport.WeeklyReportStatus;
import org.springframework.stereotype.Repository;

@Repository
public interface WeeklyReportRepository extends JpaRepository<WeeklyReport, Long>,
    WeeklyReportQueryRepository {

    Page<WeeklyReport> findByMenteeId(Long menteeId, Pageable pageable);

    Page<WeeklyReport> findByMenteeIdAndStatus(Long menteeId,
            WeeklyReportStatus status,
            Pageable pageable);

    boolean existsByMenteeIdAndWeekNumber(Long menteeId, Integer weekNumber);

    List<WeeklyReport> findAllByStatusAndEndDateBefore(WeeklyReportStatus status, LocalDate endDate);

    List<WeeklyReport> findAllByStatusAndEndDate(WeeklyReportStatus status, LocalDate endDate);

    List<WeeklyReport> findByEndDateAndStatus(LocalDate endDate, WeeklyReportStatus status);
}

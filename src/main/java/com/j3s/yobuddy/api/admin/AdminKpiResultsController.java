package com.j3s.yobuddy.api.admin;

import com.j3s.yobuddy.domain.kpi.results.dto.dashboard.DashboardOverviewResponse;
import com.j3s.yobuddy.domain.kpi.results.dto.dashboard.KpiDashboardResponse;
import com.j3s.yobuddy.domain.kpi.results.service.KpiDashboardService;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.j3s.yobuddy.domain.kpi.results.dto.response.KpiResultsListResponse;
import com.j3s.yobuddy.domain.kpi.results.dto.response.KpiResultsResponse;
import com.j3s.yobuddy.domain.kpi.results.service.KpiResultsService;
import com.j3s.yobuddy.domain.mentor.weeklyReport.dto.response.MentorWeeklyReportDetailResponse;
import com.j3s.yobuddy.domain.mentor.weeklyReport.service.MentorWeeklyReportService;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin/kpi/results")
public class AdminKpiResultsController {

    private final KpiResultsService kpiResultsService;
    private final MentorWeeklyReportService mentorWeeklyReportService;
    private final KpiDashboardService kpiDashboardService;

    @GetMapping
    public ResponseEntity<List<KpiResultsListResponse>> getResults(
        @RequestParam(required = false) Long kpiGoalId,
        @RequestParam(required = false) Long userId,
        @RequestParam(required = false) Long departmentId) {

        List<KpiResultsListResponse> list = kpiResultsService.getResults(kpiGoalId, userId, departmentId);
        return ResponseEntity.ok(list);
    }
    @PostMapping("/calculator")
    public String calculateKpiResults() {
        kpiResultsService.culculateKpiResults();
        return "계산이 시작되었습니다.";
    }
    

    @GetMapping("/{kpiResultId}")
    public ResponseEntity<KpiResultsResponse> getResultById(@PathVariable("kpiResultId") Long kpiResultId) {
        KpiResultsResponse resp = kpiResultsService.getResultById(kpiResultId);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{kpiResultId}")
    public ResponseEntity<String> deleteResult(@PathVariable("kpiResultId") Long kpiResultId) {
        // Not implemented: could soft-delete via service
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("삭제는 추후 구현됩니다.");
    }
    @GetMapping("/weeklyreports/user/{userId}")
    public ResponseEntity<List<MentorWeeklyReportDetailResponse>> getweeklyreport(@PathVariable String userId) {
        List<MentorWeeklyReportDetailResponse> resp = mentorWeeklyReportService.getWeeklyReportsByUserId(userId);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<KpiDashboardResponse> getDashboard(
        @RequestParam Long departmentId) {

        KpiDashboardResponse resp =
            kpiDashboardService.getDashboard(departmentId);

        return ResponseEntity.ok(resp);
    }

    @GetMapping("/dashboard/overview")
    public ResponseEntity<DashboardOverviewResponse> getOverview(
        @RequestParam LocalDate start,
        @RequestParam LocalDate end
    ) {
        return ResponseEntity.ok(kpiDashboardService.getOverviewByPeriod(start, end));
    }
}

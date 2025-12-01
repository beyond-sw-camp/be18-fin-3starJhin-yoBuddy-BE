package com.j3s.yobuddy.api.user;

import com.j3s.yobuddy.domain.weeklyReport.dto.request.WeeklyReportUpdateRequest;
import com.j3s.yobuddy.domain.weeklyReport.dto.response.WeeklyReportDetailResponse;
import com.j3s.yobuddy.domain.weeklyReport.dto.response.WeeklyReportSummaryResponse;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport.WeeklyReportStatus;
import com.j3s.yobuddy.domain.weeklyReport.service.WeeklyReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/{menteeId}/weekly-reports")
public class UserWeeklyReportController {

    private final WeeklyReportService weeklyReportService;

    @GetMapping
    public Page<WeeklyReportSummaryResponse> getWeeklyReports(
        @PathVariable("menteeId") Long menteeId,
        @RequestParam(value = "status", required = false) WeeklyReportStatus status,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return weeklyReportService.getWeeklyReports(
            menteeId,
            status,
            PageRequest.of(page, size)
        );
    }

    @GetMapping("/{weeklyReportId}")
    public WeeklyReportDetailResponse getWeeklyReportDetail(
        @PathVariable("menteeId") Long menteeId,
        @PathVariable("weeklyReportId") Long weeklyReportId
    ) {
        return weeklyReportService.getWeeklyReportDetail(menteeId, weeklyReportId);
    }

    @PostMapping("/{weeklyReportId}")
    public WeeklyReportDetailResponse updateWeeklyReport(
        @PathVariable("menteeId") Long menteeId,
        @PathVariable("weeklyReportId") Long weeklyReportId,
        @Valid @RequestBody WeeklyReportUpdateRequest request
    ) {
        return weeklyReportService.updateWeeklyReport(menteeId, weeklyReportId, request);
    }
}

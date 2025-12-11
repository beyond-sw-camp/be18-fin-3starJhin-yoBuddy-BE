package com.j3s.yobuddy.api.mentor;

import com.j3s.yobuddy.domain.mentor.weeklyReport.dto.request.MentorWeeklyReportFeedbackRequest;
import com.j3s.yobuddy.domain.mentor.weeklyReport.dto.response.MentorWeeklyReportDetailResponse;
import com.j3s.yobuddy.domain.mentor.weeklyReport.dto.response.MentorWeeklyReportSummaryResponse;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport.WeeklyReportStatus;
import com.j3s.yobuddy.domain.mentor.weeklyReport.service.MentorWeeklyReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mentors/{mentorId}/mentees/{menteeId}/weekly-reports")
public class MentorWeeklyReportController {

    private final MentorWeeklyReportService mentorWeeklyReportService;

    @GetMapping
    public Page<MentorWeeklyReportSummaryResponse> getMenteeWeeklyReports(
        @PathVariable("mentorId") Long mentorId,
        @PathVariable("menteeId") Long menteeId,
        @RequestParam(value = "status", required = false) WeeklyReportStatus status,
        @RequestParam(value = "weekNumber", required = false) Integer weekNumber,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return mentorWeeklyReportService.getMenteeWeeklyReports(
            mentorId,
            menteeId,
            status,
            weekNumber,
            PageRequest.of(page, size)
        );
    }

    @GetMapping("/{weeklyReportId}")
    public MentorWeeklyReportDetailResponse getWeeklyReportDetail(
        @PathVariable("mentorId") Long mentorId,
        @PathVariable("menteeId") Long menteeId,
        @PathVariable("weeklyReportId") Long weeklyReportId
    ) {
        return mentorWeeklyReportService.getWeeklyReportDetail(
            mentorId,
            menteeId,
            weeklyReportId
        );
    }

    @PatchMapping("/{weeklyReportId}/feedback")
    public MentorWeeklyReportDetailResponse submitFeedback(
        @PathVariable("mentorId") Long mentorId,
        @PathVariable("menteeId") Long menteeId,
        @PathVariable("weeklyReportId") Long weeklyReportId,
        @Valid @RequestBody MentorWeeklyReportFeedbackRequest request
    ) {
        return mentorWeeklyReportService.submitFeedback(
            mentorId,
            menteeId,
            weeklyReportId,
            request
        );
    }
}

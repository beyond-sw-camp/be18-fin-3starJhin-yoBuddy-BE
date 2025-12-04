package com.j3s.yobuddy.domain.mentor.weeklyReport.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentorWeeklyReportFeedbackRequest {

    @NotBlank(message = "멘토 피드백 내용은 필수입니다.")
    private String mentorFeedback;

    private String status;
}

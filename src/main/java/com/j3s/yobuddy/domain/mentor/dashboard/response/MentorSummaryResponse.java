package com.j3s.yobuddy.domain.mentor.dashboard.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MentorSummaryResponse {
    private long todaySessions;
    private long totalSessions;
    private long pendingFeedback;
    private long pendingEvaluation;
}
package com.j3s.yobuddy.domain.mentor.weeklyReport.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class MentorWeeklyReportAccessDeniedException extends BusinessException {

    public MentorWeeklyReportAccessDeniedException(Long mentorId, Long menteeId) {
        super("해당 멘티의 주간 리포트에 접근할 권한이 없습니다. (mentorId="
            + mentorId + ", menteeId=" + menteeId + ")", HttpStatus.FORBIDDEN);
    }
}

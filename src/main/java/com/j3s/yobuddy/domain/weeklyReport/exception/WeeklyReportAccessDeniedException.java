package com.j3s.yobuddy.domain.weeklyReport.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class WeeklyReportAccessDeniedException extends BusinessException {

    public WeeklyReportAccessDeniedException(Long menteeId) {
        super("해당 주간 리포트에 접근할 권한이 없습니다. (menteeId=" + menteeId + ")", HttpStatus.FORBIDDEN);
    }
}
package com.j3s.yobuddy.domain.weeklyReport.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class WeeklyReportNotFoundException extends BusinessException {

  public WeeklyReportNotFoundException(Long weeklyReportId) {
    super("주간 리포트를 찾을 수 없습니다. (weeklyReportId=" + weeklyReportId + ")", HttpStatus.NOT_FOUND);
  }
}

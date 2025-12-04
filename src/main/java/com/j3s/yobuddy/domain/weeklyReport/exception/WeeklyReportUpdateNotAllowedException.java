package com.j3s.yobuddy.domain.weeklyReport.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class WeeklyReportUpdateNotAllowedException extends BusinessException {

  public WeeklyReportUpdateNotAllowedException() {
    super("OVERDUE 또는 REVIEWED 상태의 주간 리포트는 수정할 수 없습니다.", HttpStatus.BAD_REQUEST);
  }
}

package com.j3s.yobuddy.domain.mentor.weeklyReport.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class WeeklyReportFeedbackNotAllowedException extends BusinessException {

  public WeeklyReportFeedbackNotAllowedException() {
    super("SUBMITTED 상태의 주간 리포트에만 피드백을 작성할 수 있습니다.", HttpStatus.BAD_REQUEST);
  }
}

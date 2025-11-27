package com.j3s.yobuddy.domain.mentor.menteeAssignment.exception;

import org.springframework.http.HttpStatus;
import com.j3s.yobuddy.common.exception.BusinessException;

public class MentorNotFoundException extends BusinessException {
    public MentorNotFoundException(Long mentorId) {
        super("존재하지 않는 멘토입니다. (mentorId=" + mentorId + ")", HttpStatus.NOT_FOUND);
    }
}

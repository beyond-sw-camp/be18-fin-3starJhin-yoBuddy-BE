package com.j3s.yobuddy.domain.mentor.menteeAssignment.exception;

import org.springframework.http.HttpStatus;
import com.j3s.yobuddy.common.exception.BusinessException;

public class AlreadyDeletedMentorException extends BusinessException {
    public AlreadyDeletedMentorException(Long mentorId) {
        super("이미 삭제된 멘토입니다. (mentorId=" + mentorId + ")", HttpStatus.CONFLICT);
    }
}

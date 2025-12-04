package com.j3s.yobuddy.domain.mentor.menteeAssignment.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class UnauthorizedMenteeAccessException extends BusinessException {
    public UnauthorizedMenteeAccessException(Long mentorId, Long menteeId) {
        super("해당 멘티에 대한 접근 권한이 없습니다. (mentorId=" + mentorId + ", menteeId=" + menteeId + ")",
            HttpStatus.FORBIDDEN);
    }
}

package com.j3s.yobuddy.domain.mentor.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import com.j3s.yobuddy.domain.user.entity.Role;
import org.springframework.http.HttpStatus;

public class InvalidMenteeRoleException extends BusinessException {
    public InvalidMenteeRoleException(Long userId, Role role) {
        super("멘티는 USER 권한만 가능합니다. (userId=" + userId + ", role=" + role + ")", HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
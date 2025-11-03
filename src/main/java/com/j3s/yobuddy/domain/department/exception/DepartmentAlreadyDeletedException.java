package com.j3s.yobuddy.domain.department.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class DepartmentAlreadyDeletedException extends BusinessException {

    public DepartmentAlreadyDeletedException(Long departmentId) {
        super("이미 삭제된 부서입니다. (id=" + departmentId + ")", HttpStatus.CONFLICT);
    }
}

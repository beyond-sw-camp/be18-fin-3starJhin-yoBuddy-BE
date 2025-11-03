package com.j3s.yobuddy.domain.department.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class DepartmentNotFoundException extends BusinessException {
    
    public DepartmentNotFoundException(Long departmentId) {
        super("존재하지 않거나 삭제된 부서입니다. (id=" + departmentId + ")", HttpStatus.NOT_FOUND);
    }
}

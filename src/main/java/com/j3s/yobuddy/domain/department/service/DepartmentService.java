package com.j3s.yobuddy.domain.department.service;

import com.j3s.yobuddy.domain.department.dto.DepartmentResponse;
import java.util.List;

public interface DepartmentService {

    List<DepartmentResponse> getDepartments();

    void createDepartment(String name);

    void updateDepartment(Long departmentId, String name);

    void deleteDepartment(Long departmentId);
}

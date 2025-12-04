package com.j3s.yobuddy.domain.department.service;

import com.j3s.yobuddy.domain.department.dto.response.DepartmentListResponse;
import com.j3s.yobuddy.domain.department.dto.response.DepartmentResponse;
import java.util.List;

public interface DepartmentService {

    List<DepartmentListResponse> getDepartments(String name);

    void createDepartment(String name);

    DepartmentListResponse updateDepartment(Long departmentId, String name);

    void deleteDepartment(Long departmentId);

    DepartmentResponse getDepartmentById(Long departmentId);
}

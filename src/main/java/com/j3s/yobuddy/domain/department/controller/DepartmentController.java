package com.j3s.yobuddy.domain.department.controller;

import com.j3s.yobuddy.domain.department.dto.DepartmentCreate;
import com.j3s.yobuddy.domain.department.dto.DepartmentResponse;
import com.j3s.yobuddy.domain.department.service.DepartmentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> getDepartments() {
        List<DepartmentResponse> departments = departmentService.getDepartments();

        return ResponseEntity.ok(departments);
    }

    @PostMapping
    public ResponseEntity<String> createDepartment(@RequestBody DepartmentCreate departmentCreate) {
        String name = departmentCreate.getName();
        departmentService.createDepartment(name);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body("부서가 성공적으로 생성되었습니다.");
    }
}

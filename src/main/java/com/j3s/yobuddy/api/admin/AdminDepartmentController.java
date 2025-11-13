package com.j3s.yobuddy.api.admin;

import com.j3s.yobuddy.domain.department.dto.request.DepartmentRequest;
import com.j3s.yobuddy.domain.department.dto.response.DepartmentListResponse;
import com.j3s.yobuddy.domain.department.dto.response.DepartmentResponse;
import com.j3s.yobuddy.domain.department.service.DepartmentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin/departments")
public class AdminDepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<List<DepartmentListResponse>> getDepartments(
        @RequestParam(required = false) String name) {
        List<DepartmentListResponse> departments = departmentService.getDepartments(name);

        return ResponseEntity.ok(departments);
    }

    @GetMapping("/{departmentId}")
    public ResponseEntity<DepartmentResponse> getDepartmentById(
        @PathVariable("departmentId") Long departmentId) {
        DepartmentResponse department = departmentService.getDepartmentById(departmentId);

        return ResponseEntity.ok(department);
    }

    @PostMapping
    public ResponseEntity<String> createDepartment(
        @RequestBody DepartmentRequest departmentRequest) {
        String name = departmentRequest.getName();
        departmentService.createDepartment(name);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body("부서가 성공적으로 생성되었습니다.");
    }

    @PatchMapping("/{departmentId}")
    public ResponseEntity<String> updateDepartment(@PathVariable("departmentId") Long departmentId,
        @RequestBody DepartmentRequest departmentRequest) {
        String name = departmentRequest.getName();
        departmentService.updateDepartment(departmentId, name);
        return ResponseEntity.ok("부서명이 성공적으로 수정되었습니다.");
    }

    @DeleteMapping("/{departmentId}")
    public ResponseEntity<String> deleteDepartment(
        @PathVariable("departmentId") Long departmentId) {
        departmentService.deleteDepartment(departmentId);

        return ResponseEntity.ok("부서가 성공적으로 삭제되었습니다.");
    }
}

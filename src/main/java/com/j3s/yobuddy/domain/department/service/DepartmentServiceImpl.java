package com.j3s.yobuddy.domain.department.service;

import com.j3s.yobuddy.domain.department.dto.response.DepartmentListResponse;
import com.j3s.yobuddy.domain.department.dto.response.DepartmentResponse;
import com.j3s.yobuddy.domain.department.entity.Department;
import com.j3s.yobuddy.domain.department.exception.DepartmentAlreadyDeletedException;
import com.j3s.yobuddy.domain.department.exception.DepartmentNotFoundException;
import com.j3s.yobuddy.domain.department.repository.DepartmentRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentListResponse> getDepartments() {
        return departmentRepository.findAllByIsDeletedFalse()
            .stream()
            .map(department -> new DepartmentListResponse(
                department.getDepartmentId(),
                department.getName(),
                department.getCreatedAt(),
                department.getUpdatedAt()
            ))
            .toList();
    }

    @Override
    @Transactional
    public void createDepartment(String name) {

        Department department = Department.builder()
            .name(name)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .isDeleted(false)
            .build();

        departmentRepository.save(department);
    }

    @Override
    @Transactional
    public DepartmentListResponse updateDepartment(Long departmentId, String name) {
        Department department = departmentRepository.findByDepartmentIdAndIsDeletedFalse(
            departmentId).orElseThrow(() -> new DepartmentNotFoundException(departmentId));

        department.update(name);

        departmentRepository.save(department);

        return DepartmentListResponse.builder()
            .departmentId(department.getDepartmentId())
            .name(department.getName())
            .createdAt(department.getCreatedAt())
            .updatedAt(department.getUpdatedAt())
            .build();
    }

    @Override
    @Transactional
    public void deleteDepartment(Long departmentId) {

        Department department = departmentRepository.findByDepartmentIdAndIsDeletedFalse(
            departmentId).orElseThrow(() -> new DepartmentNotFoundException(departmentId));

        if (department.getIsDeleted()) {
            throw new DepartmentAlreadyDeletedException(departmentId);
        }
        department.softDelete();
        departmentRepository.save(department);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Long departmentId) {

        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new DepartmentNotFoundException(departmentId));

        return DepartmentResponse.from(department);
    }

    @Override
    @Transactional
    public List<DepartmentListResponse> searchDepartmentsByName(String name) {
        List<Department> result =
            (name == null || name.isBlank())
                ? departmentRepository.findAllByIsDeletedFalse()
                : departmentRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(name);

        return result.stream()
            .map(DepartmentListResponse::from)
            .toList();
    }
}

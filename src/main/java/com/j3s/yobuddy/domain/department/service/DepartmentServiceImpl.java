package com.j3s.yobuddy.domain.department.service;

import com.j3s.yobuddy.domain.department.dto.DepartmentResponse;
import com.j3s.yobuddy.domain.department.entity.Departments;
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
    public List<DepartmentResponse> getDepartments() {
        return departmentRepository.findAllByIsDeletedFalse()
            .stream()
            .map(department -> new DepartmentResponse(
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

        Departments department = Departments.builder()
            .name(name)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .isDeleted(false)
            .build();

        departmentRepository.save(department);
    }

    @Override
    public DepartmentResponse updateDepartment(Long departmentId, String name) {
        Departments department = departmentRepository.findByDepartmentIdAndIsDeletedFalse(
            departmentId).orElseThrow(() -> new DepartmentNotFoundException(departmentId));

        department.update(name);

        departmentRepository.save(department);

        return DepartmentResponse.builder()
            .departmentId(department.getDepartmentId())
            .name(department.getName())
            .createdAt(department.getCreatedAt())
            .updatedAt(department.getUpdatedAt())
            .build();
    }

    @Override
    public void deleteDepartment(Long departmentId) {

        Departments department = departmentRepository.findByDepartmentIdAndIsDeletedFalse(
            departmentId).orElseThrow(() -> new DepartmentNotFoundException(departmentId));

        if (department.getIsDeleted()) {
            throw new DepartmentAlreadyDeletedException(departmentId);
        }
        department.softDelete();
        departmentRepository.save(department);
    }
}

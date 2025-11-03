package com.j3s.yobuddy.domain.department.service;

import com.j3s.yobuddy.domain.department.dto.DepartmentResponse;
import com.j3s.yobuddy.domain.department.entity.Departments;
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
            .build();

        departmentRepository.save(department);
    }

    @Override
    @Transactional
    public void updateDepartment(Long departmentId, String name) {

        Departments existing = departmentRepository.findByDepartmentId(departmentId);

        Departments updated = existing.toBuilder()
            .departmentId(existing.getDepartmentId())
            .name(name)
            .createdAt(existing.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .isDeleted(existing.getIsDeleted())
            .build();
        departmentRepository.save(updated);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long departmentId) {

        Departments existing = departmentRepository.findByDepartmentId(departmentId);

        Departments updated = existing.toBuilder()
            .updatedAt(LocalDateTime.now())
            .isDeleted(true)
            .build();
        departmentRepository.save(updated);
    }
}

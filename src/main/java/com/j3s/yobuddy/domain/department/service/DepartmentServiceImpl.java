package com.j3s.yobuddy.domain.department.service;

import com.j3s.yobuddy.domain.department.dto.DepartmentResponse;
import com.j3s.yobuddy.domain.department.entity.Departments;
import com.j3s.yobuddy.domain.department.repository.DepartmentRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public void createDepartment(String name) {

        Departments department = Departments.builder()
            .name(name)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        departmentRepository.save(department);
    }
}

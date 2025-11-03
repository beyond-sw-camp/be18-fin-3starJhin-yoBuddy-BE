package com.j3s.yobuddy.domain.department.service;

import com.j3s.yobuddy.domain.department.dto.DepartmentResponse;
import com.j3s.yobuddy.domain.department.repository.DepartmentRepository;
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
}

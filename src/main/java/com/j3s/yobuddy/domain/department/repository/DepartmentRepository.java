package com.j3s.yobuddy.domain.department.repository;

import com.j3s.yobuddy.domain.department.entity.Department;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findAllByIsDeletedFalse();

    Optional<Department> findByDepartmentIdAndIsDeletedFalse(Long departmentId);

    List<Department> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name);
}

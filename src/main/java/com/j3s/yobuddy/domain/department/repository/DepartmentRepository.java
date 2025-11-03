package com.j3s.yobuddy.domain.department.repository;

import com.j3s.yobuddy.domain.department.entity.Departments;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Departments, Long> {

    List<Departments> findAllByIsDeletedFalse();

    Departments findByDepartmentId(Long departmentId);
}

package com.j3s.yobuddy.domain.user.repository;

import com.j3s.yobuddy.domain.user.entity.Role;
import com.j3s.yobuddy.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndIsDeletedFalse(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByUserIdAndIsDeletedFalse(Long userId);

    List<User> findAllByIsDeletedFalse();

    @Query("SELECT u FROM User u WHERE " +
        "(:name is null OR u.name LIKE %:name%) AND " +
        "(:email is null OR u.email LIKE %:email%) AND " +
        "(:role is null OR u.role = :role) AND " +
        "u.isDeleted = false")
    Page<User> searchUsers(@Param("name") String name,
        @Param("email") String email,
        @Param("role") Role role,
        Pageable pageable);

    List<User> findByDepartment_DepartmentIdAndRole(Long departmentId, Role role);

    List<User> findByDepartment_DepartmentIdAndIsDeletedFalse(Long departmentId);
}

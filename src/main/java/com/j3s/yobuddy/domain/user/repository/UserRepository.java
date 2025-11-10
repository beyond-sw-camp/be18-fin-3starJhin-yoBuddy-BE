package com.j3s.yobuddy.domain.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.j3s.yobuddy.domain.user.entity.Role;
import com.j3s.yobuddy.domain.user.entity.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
    Optional<Users> findByEmailAndIsDeletedFalse(String email);
    Optional<Users> findByPhoneNumber(String phoneNumber);
    Optional<Users> findByUserIdAndIsDeletedFalse(Long userId);
    List<Users> findAllByIsDeletedFalse();

    @Query("SELECT u FROM Users u WHERE " +
           "(:name is null OR u.name LIKE %:name%) AND " +
           "(:email is null OR u.email LIKE %:email%) AND " +
           "(:role is null OR u.role = :role) AND " +
           "u.isDeleted = false")
    Page<Users> searchUsers(@Param("name") String name,
                          @Param("email") String email,
                          @Param("role") Role role,
                          Pageable pageable);
}

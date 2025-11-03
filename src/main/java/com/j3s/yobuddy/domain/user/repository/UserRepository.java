package com.j3s.yobuddy.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.j3s.yobuddy.domain.user.entity.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmailAndIsDeletedFalse(String email);
    Optional<Users> findByPhoneNumber(String phoneNumber);
}

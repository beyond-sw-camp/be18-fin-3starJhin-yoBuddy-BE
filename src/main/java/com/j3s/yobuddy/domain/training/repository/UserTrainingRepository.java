package com.j3s.yobuddy.domain.training.repository;

import com.j3s.yobuddy.domain.training.entity.UserTraining;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTrainingRepository extends JpaRepository<UserTraining, Long> {
}

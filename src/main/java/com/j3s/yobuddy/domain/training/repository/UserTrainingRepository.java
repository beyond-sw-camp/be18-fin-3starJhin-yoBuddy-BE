package com.j3s.yobuddy.domain.training.repository;

import com.j3s.yobuddy.domain.training.entity.UserTraining;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTrainingRepository extends JpaRepository<UserTraining, Long> {

    Optional<UserTraining> findByUser_UserIdAndProgramTraining_ProgramTrainingId(Long userId,
        Long programTrainingId);
}

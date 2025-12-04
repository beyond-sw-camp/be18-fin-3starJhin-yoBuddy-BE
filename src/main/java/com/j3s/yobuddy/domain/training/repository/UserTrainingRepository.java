package com.j3s.yobuddy.domain.training.repository;

import com.j3s.yobuddy.domain.training.entity.UserTraining;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTrainingRepository extends JpaRepository<UserTraining, Long>, UserTrainingQueryRepository {

    Optional<UserTraining> findByUser_UserIdAndProgramTraining_ProgramTrainingId(Long userId,
        Long programTrainingId);
}

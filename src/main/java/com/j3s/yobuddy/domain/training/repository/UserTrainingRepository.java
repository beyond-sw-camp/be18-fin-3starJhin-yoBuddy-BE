package com.j3s.yobuddy.domain.training.repository;

import com.j3s.yobuddy.domain.training.entity.ProgramTraining;
import com.j3s.yobuddy.domain.training.entity.UserTraining;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTrainingRepository extends JpaRepository<UserTraining, Long>,
    UserTrainingQueryRepository {

    Optional<UserTraining> findByUser_UserIdAndProgramTraining_ProgramTrainingId(Long userId,
        Long programTrainingId);

    List<UserTraining> findByUser_UserId(Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from UserTraining ut where ut.programTraining = :programTraining")
    void deleteByProgramTraining(@Param("programTraining") ProgramTraining programTraining);
}

package com.j3s.yobuddy.domain.mentor.repository;

import com.j3s.yobuddy.domain.mentor.entity.MentorMenteeAssignment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MentorMenteeAssignmentRepository
    extends JpaRepository<MentorMenteeAssignment, Long> {

    boolean existsByMenteeUserIdAndDeletedFalse(Long menteeId);

    Optional<MentorMenteeAssignment> findByMenteeUserIdAndDeletedFalse(Long menteeId);

    List<MentorMenteeAssignment> findByMentorUserIdAndDeletedFalse(Long mentorId);
}

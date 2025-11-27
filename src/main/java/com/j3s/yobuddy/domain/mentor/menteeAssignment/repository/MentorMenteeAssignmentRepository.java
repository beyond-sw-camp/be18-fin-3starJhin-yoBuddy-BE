package com.j3s.yobuddy.domain.mentor.menteeAssignment.repository;

import com.j3s.yobuddy.domain.mentor.menteeAssignment.entity.MentorMenteeAssignment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MentorMenteeAssignmentRepository extends JpaRepository<MentorMenteeAssignment, Long> {

    boolean existsByMenteeUserIdAndDeletedFalse(Long menteeId);

    Optional<MentorMenteeAssignment> findByMenteeUserIdAndDeletedFalse(Long menteeId);

    List<MentorMenteeAssignment> findByMentorUserIdAndDeletedFalse(Long mentorId);

    boolean existsByMentorUserIdAndMenteeUserIdAndDeletedFalse(Long mentorId, Long menteeId);

    @Query("""
        select m.mentee.userId 
        from MentorMenteeAssignment m 
        where m.mentor.userId = :mentorId 
          and m.deleted = false
    """)
    List<Long> findMenteeIdsByMentorUserId(Long mentorId);
}

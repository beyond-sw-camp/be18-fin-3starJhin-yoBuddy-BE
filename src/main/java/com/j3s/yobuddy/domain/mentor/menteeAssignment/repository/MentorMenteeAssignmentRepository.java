package com.j3s.yobuddy.domain.mentor.menteeAssignment.repository;

import com.j3s.yobuddy.domain.mentor.menteeAssignment.entity.MentorMenteeAssignment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MentorMenteeAssignmentRepository extends JpaRepository<MentorMenteeAssignment, Long> {

    boolean existsByMenteeUserId(Long menteeId);

    Optional<MentorMenteeAssignment> findByMenteeUserId(Long menteeId);

    List<MentorMenteeAssignment> findByMentorUserId(Long mentorId);

    boolean existsByMentorUserIdAndMenteeUserId(Long mentorId, Long menteeId);

    @Query("""
        select m.mentee.userId 
        from MentorMenteeAssignment m 
        where m.mentor.userId = :mentorId 
    """)
    List<Long> findMenteeIdsByMentorUserId(Long mentorId);
}

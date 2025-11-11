package com.j3s.yobuddy.domain.mentor.repository;

import com.j3s.yobuddy.domain.mentor.entity.Mentor;
import com.j3s.yobuddy.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MentorRepository extends JpaRepository<Mentor, Long> {

    Optional<Mentor> findByUserUserIdAndDeletedFalse(Long userId);

    boolean existsByUserAndDeletedFalse(User user);
}

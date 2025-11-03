package com.j3s.yobuddy.domain.buddies.repository;

import com.j3s.yobuddy.domain.buddies.entity.Buddies;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BuddiesRepository extends JpaRepository<Buddies, Long> {

    List<Buddies> findByUserIdAndIsDeletedFalse(Long userId);

    List<Buddies> findByPositionAndIsDeletedFalse(String position);

    List<Buddies> findByIsDeletedFalse();

    boolean existsByUserIdAndIsDeletedFalse(Long userId);
}

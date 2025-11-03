package com.j3s.yobuddy.domain.buddies.repository;

import com.j3s.yobuddy.domain.buddies.entity.Buddies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuddiesRepository extends JpaRepository<Buddies, Long> {
    List<Buddies> findByIsDeletedFalse();

    List<Buddies> findByUserIdAndIsDeletedFalse(Long userId);

    List<Buddies> findByPositionAndIsDeletedFalse(String position);
}

package com.j3s.yobuddy.domain.buddies.service;

import com.j3s.yobuddy.domain.buddies.entity.Buddies;
import com.j3s.yobuddy.domain.buddies.repository.BuddiesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BuddiesServiceImpl implements BuddiesService {

    private final BuddiesRepository buddiesRepository;

    @Override
    @Transactional
    public Buddies createBuddy(Long userId, String position) {
        Buddies buddy = Buddies.builder()
            .userId(userId)
            .position(position)
            .isDeleted(false)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        return buddiesRepository.save(buddy);
    }

    @Override
    public List<Buddies> getBuddies(Long userId, String position) {
        if (userId != null) {
            return buddiesRepository.findByUserIdAndIsDeletedFalse(userId);
        } else if (position != null) {
            return buddiesRepository.findByPositionAndIsDeletedFalse(position);
        } else {
            return buddiesRepository.findByIsDeletedFalse();
        }
    }

    @Override
    public Buddies getBuddyById(Long buddyId) {
        return buddiesRepository.findById(buddyId)
            .filter(b -> !b.isDeleted())
            .orElseThrow(() -> new IllegalArgumentException("Buddy not found: " + buddyId));
    }

    @Override
    @Transactional
    public Buddies updateBuddy(Long buddyId, String position) {
        Buddies buddy = getBuddyById(buddyId);
        buddy = Buddies.builder()
            .buddyId(buddy.getBuddyId())
            .userId(buddy.getUserId())
            .position(position)
            .createdAt(buddy.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .isDeleted(false)
            .build();
        return buddiesRepository.save(buddy);
    }

    @Override
    @Transactional
    public void deleteBuddy(Long buddyId) {
        Buddies buddy = getBuddyById(buddyId);
        buddy = Buddies.builder()
            .buddyId(buddy.getBuddyId())
            .userId(buddy.getUserId())
            .position(buddy.getPosition())
            .createdAt(buddy.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .isDeleted(true)
            .build();
        buddiesRepository.save(buddy);
    }
}

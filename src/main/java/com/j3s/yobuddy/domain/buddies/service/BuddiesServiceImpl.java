package com.j3s.yobuddy.domain.buddies.service;

import com.j3s.yobuddy.domain.buddies.entity.Buddies;
import com.j3s.yobuddy.domain.buddies.exception.AlreadyDeletedBuddyException;
import com.j3s.yobuddy.domain.buddies.exception.BuddyAlreadyAssignedException;
import com.j3s.yobuddy.domain.buddies.exception.BuddyNotFoundException;
import com.j3s.yobuddy.domain.buddies.exception.BuddyAssignmentException;
import com.j3s.yobuddy.domain.buddies.repository.BuddiesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BuddiesServiceImpl implements BuddiesService {

    private final BuddiesRepository buddiesRepository;

    // 버디 생성
    @Override
    @Transactional
    public Buddies createBuddy(Long userId, String position) {
        // 이미 버디인 경우 예외
        if (buddiesRepository.existsByUserIdAndIsDeletedFalse(userId)) {
            throw new BuddyAlreadyAssignedException(userId);
        }

        Buddies buddy = Buddies.create(userId, position);
        return buddiesRepository.save(buddy);
    }

    // 버디 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<Buddies> getBuddies(Long userId, String position) {
        if (userId != null) return buddiesRepository.findByUserIdAndIsDeletedFalse(userId);
        else if (position != null) return buddiesRepository.findByPositionAndIsDeletedFalse(position);
        else return buddiesRepository.findByIsDeletedFalse();
    }

    // 버디 상세 조회
    @Override
    @Transactional(readOnly = true)
    public Buddies getBuddyById(Long buddyId) {
        return buddiesRepository.findById(buddyId)
            .filter(b -> !b.isDeleted())
            .orElseThrow(() -> new BuddyNotFoundException(buddyId));
    }

    // 버디 수정
    @Override
    @Transactional
    public Buddies updateBuddy(Long buddyId, String position) {
        Buddies buddy = getBuddyById(buddyId);
        buddy.updatePosition(position); // 엔티티 메서드 호출
        return buddy;
    }

    // 버디 삭제
    @Override
    @Transactional
    public void deleteBuddy(Long buddyId) {
        Buddies buddy = getBuddyById(buddyId);
        if (buddy.isDeleted()) throw new AlreadyDeletedBuddyException(buddyId);
        buddy.markDeleted(); // 엔티티 메서드 호출
    }
}

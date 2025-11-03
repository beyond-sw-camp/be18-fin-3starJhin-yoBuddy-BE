package com.j3s.yobuddy.domain.buddies.service;

import com.j3s.yobuddy.domain.buddies.entity.Buddies;
import java.util.List;

public interface BuddiesService {

    Buddies createBuddy(Long userId, String position);

    List<Buddies> getBuddies(Long userId, String position);

    Buddies getBuddyById(Long buddyId);

    Buddies updateBuddy(Long buddyId, String position);

    void deleteBuddy(Long buddyId);
}

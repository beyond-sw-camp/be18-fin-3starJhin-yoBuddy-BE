package com.j3s.yobuddy.domain.user.dashboard.repository;

import com.j3s.yobuddy.domain.user.dashboard.response.MentorResponse;

public interface UserQueryRepository {

    MentorResponse getMentor(Long userId);

}

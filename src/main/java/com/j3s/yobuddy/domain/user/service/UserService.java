package com.j3s.yobuddy.domain.user.service;

import com.j3s.yobuddy.domain.user.dto.RegisterRequest;
import com.j3s.yobuddy.domain.user.dto.UpdateUserRequest;
import com.j3s.yobuddy.domain.user.dto.UserSearchRequest;
import com.j3s.yobuddy.domain.user.entity.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    List<User> register(List<RegisterRequest> reqs);

    void softDelete(Long userId);

    void update(Long userId, UpdateUserRequest req);

    Page<User> getAllUsers(UserSearchRequest searchRequest, Pageable pageable);

    User getUserById(Long userId);
}

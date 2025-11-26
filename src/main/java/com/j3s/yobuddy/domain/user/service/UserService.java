package com.j3s.yobuddy.domain.user.service;

import com.j3s.yobuddy.domain.user.dto.request.UpdateProfileRequest;
import com.j3s.yobuddy.domain.user.dto.response.UserProfileResponse;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.j3s.yobuddy.domain.user.dto.request.RegisterRequest;
import com.j3s.yobuddy.domain.user.dto.request.UpdateUserRequest;
import com.j3s.yobuddy.domain.user.dto.request.UserSearchRequest;
import com.j3s.yobuddy.domain.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    List<User> register(List<RegisterRequest> reqs);

    void softDelete(Long userId);

    void update(Long userId, UpdateUserRequest req);

    Page<User> getAllUsers(UserSearchRequest searchRequest, Pageable pageable);

    User getUserById(Long userId);

    UserProfileResponse getUserProfile(Long userId);

    void updateMyAccount(Long userId, UpdateProfileRequest req, MultipartFile profileImage);

    void deleteProfileImage(Long userId);
}

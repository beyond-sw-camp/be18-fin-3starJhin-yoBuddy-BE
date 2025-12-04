package com.j3s.yobuddy.domain.user.service;

import com.j3s.yobuddy.common.dto.FileResponse;
import com.j3s.yobuddy.domain.file.entity.FileEntity;
import com.j3s.yobuddy.domain.file.entity.FileType;
import com.j3s.yobuddy.domain.file.entity.RefType;
import com.j3s.yobuddy.domain.file.repository.FileRepository;
import com.j3s.yobuddy.domain.file.service.FileService;
import com.j3s.yobuddy.domain.user.dto.request.UpdateProfileRequest;
import com.j3s.yobuddy.domain.user.dto.response.UserProfileResponse;
import com.j3s.yobuddy.domain.user.dto.response.UserResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.j3s.yobuddy.domain.department.entity.Department;
import com.j3s.yobuddy.domain.department.exception.DepartmentNotFoundException;
import com.j3s.yobuddy.domain.department.repository.DepartmentRepository;
import com.j3s.yobuddy.domain.user.dto.request.RegisterRequest;
import com.j3s.yobuddy.domain.user.dto.request.UpdateUserRequest;
import com.j3s.yobuddy.domain.user.dto.request.UserSearchRequest;
import com.j3s.yobuddy.domain.user.entity.Role;
import com.j3s.yobuddy.domain.user.entity.User;
import com.j3s.yobuddy.domain.user.exception.InvalidUserRequestException;
import com.j3s.yobuddy.domain.user.exception.UserAlreadyDeletedException;
import com.j3s.yobuddy.domain.user.exception.UserEmailAlreadyExistsException;
import com.j3s.yobuddy.domain.user.exception.UserNotFoundException;
import com.j3s.yobuddy.domain.user.exception.UserPasswordMismatchException;
import com.j3s.yobuddy.domain.user.exception.UserPhoneAlreadyExistsException;
import com.j3s.yobuddy.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;
    private final FileRepository fileRepository;
    private final FileService fileService;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(UserSearchRequest searchRequest, Pageable pageable) {

        Page<User> page = userRepository.searchUsers(
            searchRequest.getName(),
            searchRequest.getEmail(),
            searchRequest.getRole(),
            pageable
        );

        return page.map(u -> {
            // 프로필 이미지 조회
            List<FileEntity> files =
                fileRepository.findByRefTypeAndRefId(RefType.USER_PROFILE, u.getUserId());

            String profileImageUrl = files.isEmpty()
                ? null
                : FileResponse.from(files.get(0)).getUrl();

            return UserResponse.from(u, profileImageUrl);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        List<FileEntity> files =
            fileRepository.findByRefTypeAndRefId(RefType.USER_PROFILE, userId);

        String profileImageUrl = files.isEmpty()
            ? null
            : FileResponse.from(files.get(0)).getUrl();

        return UserResponse.from(user, profileImageUrl);
    }

    @Override
    public UserProfileResponse getUserProfile(Long userId) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        // 프로필 이미지 조회
        List<FileEntity> files =
            fileRepository.findByRefTypeAndRefId(RefType.USER_PROFILE, userId);

        FileResponse profile = files.isEmpty()
            ? null
            : FileResponse.from(files.get(0));

        return UserProfileResponse.builder()
            .userId(user.getUserId())
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole().name())
            .departmentId(user.getDepartment().getDepartmentId())
            .departmentName(user.getDepartment().getName())
            .joinedAt(user.getJoinedAt().toString())
            .createdAt(user.getCreatedAt().toString())
            .updatedAt(user.getUpdatedAt().toString())
            .profileImageUrl(profile != null ? profile.getUrl() : null)
            .build();
    }

    @Override
    @Transactional
    public void updateMyAccount(Long userId, UpdateProfileRequest req, MultipartFile profileImage) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.isDeleted()) {
            throw new UserAlreadyDeletedException(userId);
        }

        if (req.getPhoneNumber() != null) {
            String phone = req.getPhoneNumber().trim();
            if (phone.isEmpty()) {
                throw new InvalidUserRequestException("연락처는 비어 있을 수 없습니다.");
            }

            userRepository.findByPhoneNumber(phone).ifPresent(existing -> {
                if (!existing.getUserId().equals(user.getUserId())) {
                    throw new UserPhoneAlreadyExistsException(phone);
                }
            });

            user.changePhoneNumber(phone);
        }

        boolean currentProvided =
            req.getCurrentPassword() != null && !req.getCurrentPassword().isBlank();
        boolean newProvided =
            req.getNewPassword() != null && !req.getNewPassword().isBlank();

        if (currentProvided || newProvided) {
            if (!currentProvided || !newProvided) {
                throw new InvalidUserRequestException("비밀번호 변경 시 현재/새 비밀번호 모두 필요합니다.");
            }
            if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
                throw new UserPasswordMismatchException();
            }
            user.changePassword(passwordEncoder.encode(req.getNewPassword()));
        }

        if (profileImage != null && !profileImage.isEmpty()) {

            List<FileEntity> oldFiles =
                fileRepository.findByRefTypeAndRefId(RefType.USER_PROFILE, userId);

            for (FileEntity old : oldFiles) {
                fileService.deleteFile(old.getFileId());
            }

            try {
                FileEntity uploaded = fileService.uploadTempFile(profileImage, FileType.USER_PROFILE);
                fileService.bindFile(uploaded.getFileId(), RefType.USER_PROFILE, userId);
            } catch (Exception e) {
                throw new RuntimeException("프로필 이미지 업로드 중 오류 발생", e);
            }
        }
    }

    @Override
    @Transactional
    public List<User> register(List<RegisterRequest> reqs) {
        if (reqs == null || reqs.isEmpty()) {
            throw new InvalidUserRequestException("등록할 사용자는 최소 1명 이상이어야 합니다.");
        }

        Set<String> emailsInBatch = new HashSet<>();
        Set<String> phonesInBatch = new HashSet<>();
        List<User> toSave = new ArrayList<>(reqs.size());
        Map<Long, Department> departmentCache = new HashMap<>();

        for (RegisterRequest req : reqs) {
            Department department = resolveDepartment(req.getDepartmentId(), departmentCache);
            User user = buildUser(req, department);

            if (!emailsInBatch.add(user.getEmail())) {
                throw new InvalidUserRequestException(
                    "요청 내 중복된 이메일입니다. (email=" + user.getEmail() + ")");
            }
            if (!phonesInBatch.add(user.getPhoneNumber())) {
                throw new InvalidUserRequestException(
                    "요청 내 중복된 연락처입니다. (phone=" + user.getPhoneNumber() + ")");
            }

            userRepository.findByEmail(user.getEmail()).ifPresent(u -> {
                throw new UserEmailAlreadyExistsException(user.getEmail());
            });
            userRepository.findByPhoneNumber(user.getPhoneNumber()).ifPresent(u -> {
                throw new UserPhoneAlreadyExistsException(user.getPhoneNumber());
            });

            toSave.add(user);
        }

        return userRepository.saveAll(toSave);
    }

    @Override
    @Transactional
    public void softDelete(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.isDeleted()) {
            throw new UserAlreadyDeletedException(userId);
        }

        user.softDelete();
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void update(Long userId, UpdateUserRequest req) {
        if (req == null) {
            throw new InvalidUserRequestException("수정할 정보를 입력하세요.");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.isDeleted()) {
            throw new UserAlreadyDeletedException(userId);
        }

        if (req.getPhoneNumber() != null) {
            String phone = req.getPhoneNumber().trim();
            if (phone.isEmpty()) {
                throw new InvalidUserRequestException("연락처는 비어 있을 수 없습니다.");
            }
            userRepository.findByPhoneNumber(phone).ifPresent(existing -> {
                if (!existing.getUserId().equals(user.getUserId())) {
                    throw new UserPhoneAlreadyExistsException(phone);
                }
            });
            user.changePhoneNumber(phone);
        }

        if (req.getDepartmentId() != null) {
            Department department = departmentRepository.findByDepartmentIdAndIsDeletedFalse(
                    req.getDepartmentId())
                .orElseThrow(() -> new DepartmentNotFoundException(req.getDepartmentId()));
            user.changeDepartment(department);
        }

        if (req.getRole() != null) {
            user.changeRole(req.getRole());
        }

        boolean currentProvided =
            req.getCurrentPassword() != null && !req.getCurrentPassword().isBlank();
        boolean newProvided = req.getNewPassword() != null && !req.getNewPassword().isBlank();
        if (currentProvided || newProvided) {
            if (!currentProvided || !newProvided) {
                throw new InvalidUserRequestException("비밀번호를 변경하려면 현재/새 비밀번호를 모두 입력해야 합니다.");
            }
            if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
                throw new UserPasswordMismatchException();
            }
            user.changePassword(passwordEncoder.encode(req.getNewPassword()));
        }
    }

    private Department resolveDepartment(Long departmentId, Map<Long, Department> cache) {
        if (departmentId == null) {
            return null;
        }

        return cache.computeIfAbsent(departmentId, id ->
            departmentRepository.findByDepartmentIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id))
        );
    }

    private User buildUser(RegisterRequest req, Department department) {
        String name = req.getName();
        if (name == null || name.isBlank()) {
            throw new InvalidUserRequestException("이름은 필수 값입니다.");
        }

        String email = req.getEmail();
        if (email == null || email.isBlank()) {
            throw new InvalidUserRequestException("이메일은 필수 값입니다.");
        }

        String password = req.getPassword();
        if (password == null || password.isBlank()) {
            throw new InvalidUserRequestException("비밀번호는 필수 값입니다.");
        }

        String phoneNumber = req.getPhoneNumber();
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new InvalidUserRequestException("연락처는 필수 값입니다.");
        }

        Role role = Objects.requireNonNullElse(req.getRole(), Role.USER);
        String encoded = passwordEncoder.encode(password);

        return User.builder()
            .name(name)
            .email(email)
            .password(encoded)
            .phoneNumber(phoneNumber)
            .role(role)
            .joinedAt(req.getJoinedAt())
            .department(department)
            .build();
    }

    @Transactional
    public void deleteProfileImage(Long userId) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        List<FileEntity> files =
            fileRepository.findByRefTypeAndRefId(RefType.USER_PROFILE, userId);

        for (FileEntity file : files) {
            fileService.deleteFile(file.getFileId());
        }
    }

    private String getProfileImageUrl(Long userId) {
        return fileRepository.findByRefTypeAndRefId(RefType.USER_PROFILE, userId)
            .stream()
            .findFirst()
            .map(FileResponse::from)
            .map(FileResponse::getUrl)
            .orElse(null);
    }
}

package com.j3s.yobuddy.domain.user.service;

import com.j3s.yobuddy.domain.mentor.entity.Mentor;
import com.j3s.yobuddy.domain.mentor.repository.MentorRepository;
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
import com.j3s.yobuddy.domain.user.dto.RegisterRequest;
import com.j3s.yobuddy.domain.user.dto.UpdateUserRequest;
import com.j3s.yobuddy.domain.user.dto.UserSearchRequest;
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

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;
    private final MentorRepository mentorRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<User> getAllUsers(UserSearchRequest searchRequest, Pageable pageable) {
        return userRepository.searchUsers(
            searchRequest.getName(),
            searchRequest.getEmail(),
            searchRequest.getRole(),
            pageable
        );
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
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
                    "요청 내에서 중복된 이메일입니다. (email=" + user.getEmail() + ")");
            }
            if (!phonesInBatch.add(user.getPhoneNumber())) {
                throw new InvalidUserRequestException(
                    "요청 내에서 중복된 연락처입니다. (phoneNumber=" + user.getPhoneNumber() + ")");
            }

            userRepository.findByEmail(user.getEmail()).ifPresent(u -> {
                throw new UserEmailAlreadyExistsException(user.getEmail());
            });
            userRepository.findByPhoneNumber(user.getPhoneNumber()).ifPresent(u -> {
                throw new UserPhoneAlreadyExistsException(user.getPhoneNumber());
            });

            toSave.add(user);
        }

        List<User> savedUsers = userRepository.saveAll(toSave);

        for (int i = 0; i < savedUsers.size(); i++) {
            User user = savedUsers.get(i);
            RegisterRequest req = reqs.get(i);

            if (user.getRole() == Role.MENTOR) {
                String position = (req.getPosition() != null && !req.getPosition()
                    .isBlank())
                    ? req.getPosition()
                    : "신규 멘토";

                Mentor mentor = Mentor.create(user, position);
                mentorRepository.save(mentor);
            }
        }
        return savedUsers;
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
}

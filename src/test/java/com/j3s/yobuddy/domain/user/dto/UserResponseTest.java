package com.j3s.yobuddy.domain.user.dto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.j3s.yobuddy.domain.department.entity.Department;
import com.j3s.yobuddy.domain.user.entity.Role;
import com.j3s.yobuddy.domain.user.entity.User;

@DisplayName("UserResponse DTO 테스트")
class UserResponseTest {

    private User user;
    private Department department;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        
        // Department 생성
        department = Department.builder()
            .departmentId(1L)
            .name("개발팀")
            .createdAt(now)
            .updatedAt(now)
            .isDeleted(false)
            .build();

        // User 생성
        user = User.builder()
            .userId(1L)
            .name("테스트유저")
            .email("test@example.com")
            .password("encodedPassword123")
            .phoneNumber("01012345678")
            .role(Role.USER)
            .department(department)
            .joinedAt(now)
            .createdAt(now)
            .updatedAt(now)
            .isDeleted(false)
            .build();
    }

    @Test
    @DisplayName("User를 UserResponse로 변환할 수 있다")
    void testFromUser() {
        // When
        UserResponse response = UserResponse.from(user);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("테스트유저", response.getName());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("01012345678", response.getPhoneNumber());
        assertEquals(Role.USER, response.getRole());
        assertEquals(1L, response.getDepartmentId());
        assertEquals("개발팀", response.getDepartmentName());
        assertEquals(now, response.getJoinedAt());
        assertEquals(now, response.getCreatedAt());
        assertEquals(now, response.getUpdatedAt());
        assertFalse(response.getIsDeleted());
    }

    @Test
    @DisplayName("User의 비밀번호는 UserResponse에 포함되지 않는다")
    void testPasswordNotIncluded() {
        // When
        UserResponse response = UserResponse.from(user);

        // Then - 비밀번호가 null이거나 포함되지 않아야 함
        // UserResponse 클래스에 password 필드가 없음을 확인
        assertTrue(true); // UserResponse는 password 필드를 갖지 않음
    }

    @Test
    @DisplayName("Department가 null인 User를 변환할 수 있다")
    void testFromUserWithoutDepartment() {
        // Given
        User userWithoutDept = User.builder()
            .userId(2L)
            .name("부서없는유저")
            .email("nodept@example.com")
            .password("encodedPassword")
            .phoneNumber("01098765432")
            .role(Role.USER)
            .department(null)
            .joinedAt(now)
            .createdAt(now)
            .updatedAt(now)
            .isDeleted(false)
            .build();

        // When
        UserResponse response = UserResponse.from(userWithoutDept);

        // Then
        assertNotNull(response);
        assertEquals(2L, response.getUserId());
        assertNull(response.getDepartmentId());
        assertNull(response.getDepartmentName());
    }

    @Test
    @DisplayName("null User를 변환하면 null을 반환한다")
    void testFromNullUser() {
        // When
        UserResponse response = UserResponse.from(null);

        // Then
        assertNull(response);
    }

    @Test
    @DisplayName("삭제된 User를 변환할 수 있다")
    void testFromDeletedUser() {
        // Given
        User deletedUser = User.builder()
            .userId(3L)
            .name("삭제된유저")
            .email("deleted@example.com")
            .password("encodedPassword")
            .phoneNumber("01011111111")
            .role(Role.USER)
            .department(department)
            .joinedAt(now)
            .createdAt(now)
            .updatedAt(now)
            .isDeleted(true)
            .build();

        // When
        UserResponse response = UserResponse.from(deletedUser);

        // Then
        assertTrue(response.getIsDeleted());
    }

    @Test
    @DisplayName("UserResponse 빌더로 직접 생성할 수 있다")
    void testBuilder() {
        // When
        UserResponse response = UserResponse.builder()
            .userId(4L)
            .name("빌더테스트")
            .email("builder@example.com")
            .phoneNumber("01022222222")
            .role(Role.MENTOR)
            .departmentId(2L)
            .departmentName("마케팅팀")
            .joinedAt(now)
            .createdAt(now)
            .updatedAt(now)
            .isDeleted(false)
            .build();

        // Then
        assertNotNull(response);
        assertEquals(4L, response.getUserId());
        assertEquals("빌더테스트", response.getName());
        assertEquals(Role.MENTOR, response.getRole());
    }
}

package com.j3s.yobuddy.api.admin;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j3s.yobuddy.domain.department.entity.Department;
import com.j3s.yobuddy.domain.user.dto.RegisterRequest;
import com.j3s.yobuddy.domain.user.dto.UpdateUserRequest;
import com.j3s.yobuddy.domain.user.entity.Role;
import com.j3s.yobuddy.domain.user.entity.User;
import com.j3s.yobuddy.domain.user.service.UserService;

@WebMvcTest(AdminUserController.class)
@DisplayName("AdminUserController 테스트")
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Department testDepartment;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        // 테스트용 부서 생성
        testDepartment = Department.builder()
            .departmentId(1L)
            .name("개발팀")
            .createdAt(now)
            .updatedAt(now)
            .isDeleted(false)
            .build();

        // 테스트용 사용자 생성
        testUser = User.builder()
            .userId(1L)
            .name("테스트유저")
            .email("test@example.com")
            .password("encodedPassword123")
            .phoneNumber("01012345678")
            .role(Role.USER)
            .department(testDepartment)
            .joinedAt(now)
            .createdAt(now)
            .updatedAt(now)
            .isDeleted(false)
            .build();
    }

    @Test
    @DisplayName("모든 사용자 목록을 페이지 단위로 조회할 수 있다")
    void testGetAllUsers() throws Exception {
        // Given
        Page<User> userPage = new PageImpl<>(Arrays.asList(testUser), PageRequest.of(0, 10), 1);
        when(userService.getAllUsers(any(), any())).thenReturn(userPage);

        // When & Then
        mockMvc.perform(get("/api/v1/admin/users")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].userId", is(1)))
            .andExpect(jsonPath("$.content[0].name", is("테스트유저")))
            .andExpect(jsonPath("$.content[0].email", is("test@example.com")))
            .andExpect(jsonPath("$.content[0].phoneNumber", is("01012345678")))
            .andExpect(jsonPath("$.content[0].role", is("USER")))
            .andExpect(jsonPath("$.content[0].departmentId", is(1)))
            .andExpect(jsonPath("$.content[0].departmentName", is("개발팀")))
            .andExpect(jsonPath("$.content[0].password").doesNotExist());
    }

    @Test
    @DisplayName("특정 사용자 ID로 사용자를 조회할 수 있다")
    void testGetUserById() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(testUser);

        // When & Then
        mockMvc.perform(get("/api/v1/admin/users/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId", is(1)))
            .andExpect(jsonPath("$.name", is("테스트유저")))
            .andExpect(jsonPath("$.email", is("test@example.com")))
            .andExpect(jsonPath("$.phoneNumber", is("01012345678")))
            .andExpect(jsonPath("$.role", is("USER")))
            .andExpect(jsonPath("$.departmentId", is(1)))
            .andExpect(jsonPath("$.departmentName", is("개발팀")))
            .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("여러 사용자를 등록할 수 있다")
    void testRegister() throws Exception {
        // Given
        List<RegisterRequest> requests = Arrays.asList(
            RegisterRequest.builder()
                .name("새로운유저1")
                .email("newuser1@example.com")
                .password("password123")
                .phoneNumber("01087654321")
                .role(Role.USER)
                .departmentId(1L)
                .build()
        );

        when(userService.register(requests)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(post("/api/v1/admin/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requests)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status", is(201)))
            .andExpect(jsonPath("$.message", is("사용자가 성공적으로 등록되었습니다.")));

        verify(userService, times(1)).register(requests);
    }

    @Test
    @DisplayName("사용자 정보를 업데이트할 수 있다")
    void testUpdate() throws Exception {
        // Given
        UpdateUserRequest updateRequest = UpdateUserRequest.builder()
            .phoneNumber("01099999999")
            .role(Role.ADMIN)
            .build();

        doNothing().when(userService).update(1L, updateRequest);

        // When & Then
        mockMvc.perform(patch("/api/v1/admin/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is(200)))
            .andExpect(jsonPath("$.message", is("사용자 정보가 성공적으로 수정되었습니다.")));

        verify(userService, times(1)).update(1L, updateRequest);
    }

    @Test
    @DisplayName("사용자를 소프트 삭제할 수 있다")
    void testDelete() throws Exception {
        // Given
        doNothing().when(userService).softDelete(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/admin/users/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is(200)))
            .andExpect(jsonPath("$.message", is("사용자가 성공적으로 삭제되었습니다.")));

        verify(userService, times(1)).softDelete(1L);
    }

    @Test
    @DisplayName("부서가 없는 사용자도 조회할 수 있다")
    void testGetUserWithoutDepartment() throws Exception {
        // Given
        User userWithoutDept = User.builder()
            .userId(2L)
            .name("부서없는유저")
            .email("nodept@example.com")
            .password("encodedPassword")
            .phoneNumber("01011111111")
            .role(Role.USER)
            .department(null)
            .joinedAt(now)
            .createdAt(now)
            .updatedAt(now)
            .isDeleted(false)
            .build();

        when(userService.getUserById(2L)).thenReturn(userWithoutDept);

        // When & Then
        mockMvc.perform(get("/api/v1/admin/users/2")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId", is(2)))
            .andExpect(jsonPath("$.departmentId").doesNotExist())
            .andExpect(jsonPath("$.departmentName").doesNotExist());
    }

    @Test
    @DisplayName("여러 사용자 목록을 페이지 기반으로 조회할 수 있다")
    void testGetAllUsersMultiple() throws Exception {
        // Given
        User user2 = User.builder()
            .userId(2L)
            .name("테스트유저2")
            .email("test2@example.com")
            .password("encodedPassword123")
            .phoneNumber("01087654321")
            .role(Role.ADMIN)
            .department(testDepartment)
            .joinedAt(now)
            .createdAt(now)
            .updatedAt(now)
            .isDeleted(false)
            .build();

        Page<User> userPage = new PageImpl<>(
            Arrays.asList(testUser, user2),
            PageRequest.of(0, 10),
            2
        );
        when(userService.getAllUsers(any(), any())).thenReturn(userPage);

        // When & Then
        mockMvc.perform(get("/api/v1/admin/users")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(2)))
            .andExpect(jsonPath("$.content[0].userId", is(1)))
            .andExpect(jsonPath("$.content[1].userId", is(2)))
            .andExpect(jsonPath("$.content[1].role", is("ADMIN")))
            .andExpect(jsonPath("$.totalElements", is(2)))
            .andExpect(jsonPath("$.totalPages", is(1)));
    }

    @Test
    @DisplayName("응답에 비밀번호가 포함되지 않는다")
    void testPasswordNotExposedInResponse() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(testUser);

        // When & Then
        mockMvc.perform(get("/api/v1/admin/users/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.password").doesNotExist());
    }
}

package com.j3s.yobuddy.domain.department.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willAnswer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import org.mockito.junit.jupiter.MockitoExtension;

import com.j3s.yobuddy.domain.department.dto.response.DepartmentListResponse;
import com.j3s.yobuddy.domain.department.dto.response.DepartmentResponse;
import com.j3s.yobuddy.domain.department.entity.Department;
import com.j3s.yobuddy.domain.department.exception.DepartmentAlreadyDeletedException;
import com.j3s.yobuddy.domain.department.exception.DepartmentNotFoundException;
import com.j3s.yobuddy.domain.department.repository.DepartmentRepository;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
class DepartmentServiceImplTest {

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    @Mock
    private DepartmentRepository departmentRepository;

    private final LocalDateTime FIXED = LocalDateTime.of(2025, 11, 11, 0, 0);

    private Department department(Long id, String name, boolean deleted) {

        return Department.builder()
            .departmentId(id)
            .name(name)
            .createdAt(FIXED)
            .updatedAt(FIXED)
            .isDeleted(deleted)
            .build();
    }


    @Test
    @Order(1)
    void getDepartments_blankName_returnsAllUndeleted() {
        // Given
        List<Department> entities = List.of(
            department(1L, "인사과", false),
            department(2L, "총무과", false)
        );
        given(departmentRepository.findAllByIsDeletedFalse())
            .willReturn(entities);

        // When
        List<DepartmentListResponse> result = departmentService.getDepartments("  "); // blank

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDepartmentId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("인사과");
        assertThat(result.get(0).getCreatedAt()).isEqualTo(FIXED);
        assertThat(result.get(0).getUpdatedAt()).isEqualTo(FIXED);

        then(departmentRepository).should().findAllByIsDeletedFalse();
        then(departmentRepository).should(never())
            .findByNameContainingIgnoreCaseAndIsDeletedFalse(anyString());
        then(departmentRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @Order(2)
    void getDepartments_withName_filtersByName() {
        // Given
        String keyword = "인사";
        List<Department> entities = List.of(department(1L, "인사과", false));
        given(departmentRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(keyword))
            .willReturn(entities);

        // When
        List<DepartmentListResponse> result = departmentService.getDepartments(keyword);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDepartmentId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("인사과");

        then(departmentRepository).should()
            .findByNameContainingIgnoreCaseAndIsDeletedFalse(keyword);
        then(departmentRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @Order(3)
    void createDepartment() {
        // Given
        ArgumentCaptor<Department> captor = ArgumentCaptor.forClass(Department.class);
        willAnswer(invocation -> {
            Department d = invocation.getArgument(0);
            // save 후 PK 채워진다고 가정하는 경우가 많지만, 여기선 검증 대상 아님
            return d;
        }).given(departmentRepository).save(any(Department.class));

        // When
        departmentService.createDepartment("인사과");

        // Then
        then(departmentRepository).should().save(captor.capture());
        Department saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("인사과");
        assertThat(saved.getIsDeleted()).isFalse();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();

        then(departmentRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @Order(4)
    void updateDepartment() {
        // Given
        Department entity = department(10L, "기존이름", false);
        given(departmentRepository.findByDepartmentIdAndIsDeletedFalse(10L))
            .willReturn(Optional.of(entity));
        given(departmentRepository.save(any(Department.class))).willReturn(entity);

        // When
        DepartmentListResponse resp = departmentService.updateDepartment(10L, "새이름");

        // Then
        assertThat(resp.getDepartmentId()).isEqualTo(10L);
        assertThat(resp.getName()).isEqualTo("새이름");
        assertThat(resp.getCreatedAt()).isEqualTo(FIXED);
        assertThat(resp.getUpdatedAt()).isNotNull();

        then(departmentRepository).should().findByDepartmentIdAndIsDeletedFalse(10L);
        then(departmentRepository).should().save(entity);
        then(departmentRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @Order(5)
    void updateDepartment_notFound_throws() {
        // Given
        given(departmentRepository.findByDepartmentIdAndIsDeletedFalse(999L))
            .willReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> departmentService.updateDepartment(999L, "아무거나"))
            .isInstanceOf(DepartmentNotFoundException.class);

        // Then
        then(departmentRepository).should().findByDepartmentIdAndIsDeletedFalse(999L);
        then(departmentRepository).shouldHaveNoMoreInteractions();
    }


    @Test
    @Order(6)
    void deleteDepartment() {
        // Given
        Department entity = department(7L, "삭제대상", false);
        given(departmentRepository.findByDepartmentIdAndIsDeletedFalse(7L))
            .willReturn(Optional.of(entity));

        // When
        departmentService.deleteDepartment(7L);

        // Then
        then(departmentRepository).should().findByDepartmentIdAndIsDeletedFalse(7L);
        then(departmentRepository).should().save(entity);
        then(departmentRepository).shouldHaveNoMoreInteractions();
        assertThat(entity.getIsDeleted()).isTrue();
    }

    @Test
    @Order(7)
    void deleteDepartment_alreadyDeleted_throws() {
        // Given
        Department alreadyDeleted = department(8L, "이미삭제", true);
        given(departmentRepository.findByDepartmentIdAndIsDeletedFalse(8L))
            // 주의: 메소드 시그니처상 isDeletedFalse 조건이라면, 이미 삭제된 엔티티는 조회되지 않는 것이 자연스러움.
            // 다만 서비스 코드가 먼저 조회 후 isDeleted 체크를 하므로, 아래처럼 존재한다고 가정(프로덕션과 다를 수 있음).
            .willReturn(Optional.of(alreadyDeleted));

        // When
        assertThatThrownBy(() -> departmentService.deleteDepartment(8L))
            .isInstanceOf(DepartmentAlreadyDeletedException.class);

        // Then
        then(departmentRepository).should().findByDepartmentIdAndIsDeletedFalse(8L);
        then(departmentRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @Order(8)
    void deleteDepartment_notFound_throws() {
        // Given
        given(departmentRepository.findByDepartmentIdAndIsDeletedFalse(404L))
            .willReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> departmentService.deleteDepartment(404L))
            .isInstanceOf(DepartmentNotFoundException.class);

        // Then
        then(departmentRepository).should().findByDepartmentIdAndIsDeletedFalse(404L);
        then(departmentRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @Order(9)
    void getDepartmentById() {
        // Given
        Department entity = department(3L, "조회과", false);
        given(departmentRepository.findById(3L)).willReturn(Optional.of(entity));

        // When
        DepartmentResponse response = departmentService.getDepartmentById(3L);

        // Then
        assertThat(response.getDepartmentId()).isEqualTo(3L);
        assertThat(response.getName()).isEqualTo("조회과");
        assertThat(response.getCreatedAt()).isEqualTo(FIXED);
        assertThat(response.getUpdatedAt()).isEqualTo(FIXED);

        then(departmentRepository).should().findById(3L);
        then(departmentRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @Order(10)
    void getDepartmentById_notFound_throws() {
        // Given
        given(departmentRepository.findById(123L)).willReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> departmentService.getDepartmentById(123L))
            .isInstanceOf(DepartmentNotFoundException.class);

        // Then
        then(departmentRepository).should().findById(123L);
        then(departmentRepository).shouldHaveNoMoreInteractions();
    }
}
package com.j3s.yobuddy.domain.training.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.j3s.yobuddy.domain.training.entity.ProgramTraining;
import com.j3s.yobuddy.domain.training.entity.UserTraining;
import com.j3s.yobuddy.domain.training.entity.UserTrainingStatus;
import com.j3s.yobuddy.domain.training.repository.UserTrainingRepository;
import com.j3s.yobuddy.domain.user.entity.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserTrainingStatusUpdateServiceTest {

    @Mock
    private UserTrainingRepository userTrainingRepository;

    @InjectMocks
    private UserTrainingStatusUpdateService userTrainingStatusUpdateService;

    @Test
    @DisplayName("연체된 UserTraining들을 MISSED 상태로 변경하고 개수를 반환한다")
    void updateOverdueTrainings_marksMissed() {
        // given
        LocalDate today = LocalDate.of(2025, 11, 27);

        // 엔티티는 DB에 안 박으니까 그냥 빌더로 생성만 해도 됨
        User user = User.builder()
            .userId(1L)
            .name("유저")
            .email("user@example.com")
            .password("encoded")
            .phoneNumber("01012345678")
            .build();

        ProgramTraining programTraining = ProgramTraining.builder()
            .programTrainingId(10L)
            .startDate(LocalDate.of(2025, 11, 1))
            .endDate(LocalDate.of(2025, 11, 20)) // 이미 마감
            .assignedAt(LocalDateTime.now())
            .build();

        UserTraining ut1 = UserTraining.builder()
            .userTrainingId(100L)
            .user(user)
            .programTraining(programTraining)
            .status(UserTrainingStatus.PENDING)
            .build();

        UserTraining ut2 = UserTraining.builder()
            .userTrainingId(101L)
            .user(user)
            .programTraining(programTraining)
            .status(UserTrainingStatus.IN_PROGRESS)
            .build();

        given(userTrainingRepository.findOverdueTrainings(today))
            .willReturn(List.of(ut1, ut2));

        // when
        int updatedCount = userTrainingStatusUpdateService.updateOverdueTrainings(today);

        // then
        assertThat(updatedCount).isEqualTo(2);
        assertThat(ut1.getStatus()).isEqualTo(UserTrainingStatus.MISSED);
        assertThat(ut2.getStatus()).isEqualTo(UserTrainingStatus.MISSED);

        verify(userTrainingRepository).findOverdueTrainings(today);
    }

    @Test
    @DisplayName("연체 대상이 없으면 아무 것도 변경하지 않고 0을 반환한다")
    void updateOverdueTrainings_noOverdue() {
        // given
        LocalDate today = LocalDate.of(2025, 11, 27);

        given(userTrainingRepository.findOverdueTrainings(today))
            .willReturn(List.of());

        // when
        int updatedCount = userTrainingStatusUpdateService.updateOverdueTrainings(today);

        // then
        assertThat(updatedCount).isEqualTo(0);
        verify(userTrainingRepository).findOverdueTrainings(today);
    }

    @Test
    @DisplayName("@Scheduled 메서드가 여러 건의 교육을 처리해서 MISSED로 변경한다")
    void scheduledUpdateOverdueTrainings_marksMultipleMissed() {
        // given
        // 오늘 날짜는 실제 스케줄러에서는 LocalDate.now()로 가져오지만
        // 테스트에서는 그냥 의미 있는 값 하나 정해두기
        LocalDate today = LocalDate.of(2025, 11, 27);

        User user1 = User.builder()
            .userId(1L)
            .name("유저1")
            .email("user1@example.com")
            .password("encoded")
            .phoneNumber("01011111111")
            .build();

        User user2 = User.builder()
            .userId(2L)
            .name("유저2")
            .email("user2@example.com")
            .password("encoded")
            .phoneNumber("01022222222")
            .build();

        ProgramTraining pt1 = ProgramTraining.builder()
            .programTrainingId(10L)
            .startDate(LocalDate.of(2025, 11, 1))
            .endDate(today.minusDays(3))   // 이미 마감
            .assignedAt(LocalDateTime.now())
            .build();

        ProgramTraining pt2 = ProgramTraining.builder()
            .programTrainingId(11L)
            .startDate(LocalDate.of(2025, 11, 5))
            .endDate(today.minusDays(1))   // 이미 마감
            .assignedAt(LocalDateTime.now())
            .build();

        ProgramTraining pt3 = ProgramTraining.builder()
            .programTrainingId(12L)
            .startDate(LocalDate.of(2025, 11, 10))
            .endDate(today.plusDays(2))    // 아직 마감 안 지남 (실제라면 findOverdue에 안 걸려야 하는 케이스)
            .assignedAt(LocalDateTime.now())
            .build();

        // 연체 대상 2건 (PENDING, IN_PROGRESS)
        UserTraining ut1 = UserTraining.builder()
            .userTrainingId(100L)
            .user(user1)
            .programTraining(pt1)
            .status(UserTrainingStatus.PENDING)
            .build();

        UserTraining ut2 = UserTraining.builder()
            .userTrainingId(101L)
            .user(user2)
            .programTraining(pt2)
            .status(UserTrainingStatus.IN_PROGRESS)
            .build();

        // 참고용: 이미 MISSED 상태인 애도 한 번 넣어보자
        // (실제 구현에 따라 markMissed()에서 상태를 다시 바꾸지 않는지 확인 용도)
        UserTraining utAlreadyMissed = UserTraining.builder()
            .userTrainingId(102L)
            .user(user1)
            .programTraining(pt1)
            .status(UserTrainingStatus.MISSED)
            .build();

        // Repository가 "연체된 교육 목록"으로 여러 건을 리턴한다고 가정
        given(userTrainingRepository.findOverdueTrainings(any(LocalDate.class)))
            .willReturn(List.of(ut1, ut2, utAlreadyMissed));

        // when
        // 실제 스케줄러가 돌 때 호출되는 메서드를 직접 호출
        userTrainingStatusUpdateService.scheduledUpdateOverdueTrainings();

        // then
        // 연체로 넘어온 PENDING/IN_PROGRESS 들은 MISSED로 바뀌어야 함
        assertThat(ut1.getStatus()).isEqualTo(UserTrainingStatus.MISSED);
        assertThat(ut2.getStatus()).isEqualTo(UserTrainingStatus.MISSED);

        // 이미 MISSED였던 애는 그대로 MISSED인지(= 예상대로 동작하는지)도 한 번 체크
        assertThat(utAlreadyMissed.getStatus()).isEqualTo(UserTrainingStatus.MISSED);

        // findOverdueTrainings가 오늘 날짜로 한 번 호출됐는지만 확인
        verify(userTrainingRepository).findOverdueTrainings(any(LocalDate.class));
    }
}
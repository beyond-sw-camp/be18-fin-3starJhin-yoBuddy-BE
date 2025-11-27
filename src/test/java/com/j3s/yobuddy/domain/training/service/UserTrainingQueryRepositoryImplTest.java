package com.j3s.yobuddy.domain.training.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.j3s.yobuddy.config.QuerydslTestConfig;
import com.j3s.yobuddy.domain.department.entity.Department;
import com.j3s.yobuddy.domain.onboarding.entity.OnboardingProgram;
import com.j3s.yobuddy.domain.training.entity.ProgramTraining;
import com.j3s.yobuddy.domain.training.entity.Training;
import com.j3s.yobuddy.domain.training.entity.TrainingType;
import com.j3s.yobuddy.domain.training.entity.UserTraining;
import com.j3s.yobuddy.domain.training.entity.UserTrainingStatus;
import com.j3s.yobuddy.domain.training.repository.UserTrainingRepository;
import com.j3s.yobuddy.domain.user.entity.Role;
import com.j3s.yobuddy.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslTestConfig.class)
class UserTrainingQueryRepositoryImplTest {

    private static final Logger log =
        LoggerFactory.getLogger(UserTrainingQueryRepositoryImplTest.class);

    @Autowired
    private UserTrainingRepository userTrainingRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("endDate가 오늘보다 이전이고, 상태가 COMPLETED/MISSED가 아닌 UserTraining만 조회된다")
    void findOverdueTrainings_basic() {

        // given
        LocalDate today = LocalDate.of(2025, 11, 27);

        Department dept = Department.builder()
            .name("개발팀")
            .isDeleted(false)
            .build();
        em.persist(dept);

        // 유저
        User user = User.builder()
            .name("테스트 유저")
            .email("test@example.com")
            .password("encoded") // 실제로는 인코딩된 값
            .phoneNumber("01012345678")
            .role(Role.USER)
            .department(dept)
            .build();
        em.persist(user);

        // 온보딩 프로그램 (엔티티 정의에 맞춰 필수 필드만 채워두면 됨)
        OnboardingProgram program = OnboardingProgram.builder()
            .name("온보딩 1기")
            .description("설명")
            .startDate(LocalDate.of(2025, 11, 1))
            .endDate(LocalDate.of(2025, 11, 30))
            .department(dept)
            .build();
        em.persist(program);

// 3. 교육 3개
        Training trainingOverdue = Training.create(
            "보안 교육-연체용",
            TrainingType.ONLINE,
            "설명",
            "https://example.com/overdue"
        );
        em.persist(trainingOverdue);

        Training trainingFuture = Training.create(
            "보안 교육-미래용",
            TrainingType.ONLINE,
            "설명",
            "https://example.com/future"
        );
        em.persist(trainingFuture);

        Training trainingCompleted = Training.create(
            "보안 교육-완료용",
            TrainingType.ONLINE,
            "설명",
            "https://example.com/completed"
        );
        em.persist(trainingCompleted);

// ① 연체 대상
        ProgramTraining ptOverdue = ProgramTraining.builder()
            .program(program)
            .training(trainingOverdue)
            .assignedAt(LocalDateTime.now())
            .startDate(LocalDate.of(2025, 11, 1))
            .endDate(today.minusDays(1))
            .build();
        em.persist(ptOverdue);

        UserTraining utOverdue = UserTraining.builder()
            .user(user)
            .programTraining(ptOverdue)
            .status(UserTrainingStatus.PENDING)
            .build();
        em.persist(utOverdue);

// ② 아직 마감 안 지난 교육
        ProgramTraining ptFuture = ProgramTraining.builder()
            .program(program)
            .training(trainingFuture)
            .assignedAt(LocalDateTime.now())
            .startDate(LocalDate.of(2025, 11, 1))
            .endDate(today.plusDays(1))
            .build();
        em.persist(ptFuture);

        UserTraining utFuture = UserTraining.builder()
            .user(user)
            .programTraining(ptFuture)
            .status(UserTrainingStatus.PENDING)
            .build();
        em.persist(utFuture);

// ③ 이미 완료된 교육
        ProgramTraining ptCompleted = ProgramTraining.builder()
            .program(program)
            .training(trainingCompleted)
            .assignedAt(LocalDateTime.now())
            .startDate(LocalDate.of(2025, 11, 1))
            .endDate(today.minusDays(2))
            .build();
        em.persist(ptCompleted);

        UserTraining utCompleted = UserTraining.builder()
            .user(user)
            .programTraining(ptCompleted)
            .status(UserTrainingStatus.COMPLETED)
            .build();
        em.persist(utCompleted);

        em.flush();
        em.clear();

        // when
        List<UserTraining> result = userTrainingRepository.findOverdueTrainings(today);

        log.info("overdue 결과 개수 = {}", result.size());
        result.forEach(ut -> {
            log.info(
                "UT id={}, status={}, endDate={}, userId={}",
                ut.getUserTrainingId(),
                ut.getStatus(),
                ut.getProgramTraining().getEndDate(),
                ut.getUser().getUserId()
            );
        });

        // then
        assertThat(result)
            .extracting(UserTraining::getUserTrainingId)
            .containsExactly(utOverdue.getUserTrainingId()); // 오직 ①만 나와야 함

        assertThat(result.get(0).getStatus()).isEqualTo(UserTrainingStatus.PENDING);
        assertThat(result.get(0).getProgramTraining().getEndDate())
            .isBefore(today);
    }
}

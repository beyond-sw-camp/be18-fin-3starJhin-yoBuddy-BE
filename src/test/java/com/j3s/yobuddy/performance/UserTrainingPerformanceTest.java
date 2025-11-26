package com.j3s.yobuddy.performance;

import static com.j3s.yobuddy.domain.user.entity.Role.USER;

import com.j3s.yobuddy.domain.department.entity.Department;
import com.j3s.yobuddy.domain.department.repository.DepartmentRepository;
import com.j3s.yobuddy.domain.onboarding.entity.OnboardingProgram;
import com.j3s.yobuddy.domain.onboarding.repository.OnboardingProgramRepository;
import com.j3s.yobuddy.domain.training.entity.ProgramTraining;
import com.j3s.yobuddy.domain.training.entity.Training;
import com.j3s.yobuddy.domain.training.entity.TrainingType;
import com.j3s.yobuddy.domain.training.entity.UserTrainingStatus;
import com.j3s.yobuddy.domain.training.repository.ProgramTrainingRepository;
import com.j3s.yobuddy.domain.training.repository.TrainingRepository;
import com.j3s.yobuddy.domain.training.repository.UserTrainingRepository;
import com.j3s.yobuddy.domain.training.entity.UserTraining;
import com.j3s.yobuddy.domain.training.service.UserTrainingAssignmentService;
import com.j3s.yobuddy.domain.user.entity.User;
import com.j3s.yobuddy.domain.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class UserTrainingPerformanceTest {

    private static final int SIZE = 100_000;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private ProgramTrainingRepository ptRepository;

    @Autowired
    private UserTrainingRepository userTrainingRepository;

    @Autowired
    private UserTrainingAssignmentService assignmentService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private OnboardingProgramRepository programRepository;

    private ProgramTraining setupTestData() {

        Department dept = departmentRepository.save(
            Department.builder()
                .name("테스트부서")
                .isDeleted(false)
                .build()
        );

        OnboardingProgram program = programRepository.save(
            OnboardingProgram.builder()
                .name("대량테스트 프로그램")
                .department(dept)
                .status(OnboardingProgram.ProgramStatus.ACTIVE)
                .build()
        );

        Training training = trainingRepository.save(
            Training.create("교육A", TrainingType.ONLINE, "desc", null)
        );

        return ptRepository.save(
            ProgramTraining.builder()
                .program(program)
                .training(training)
                .build()
        );
    }

    private List<UserTraining> buildUserTrainings(ProgramTraining pt) {

        List<User> users = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            users.add(
                userRepository.save(
                    User.builder()
                        .name("user" + i)
                        .email("u" + i + "@test.com")
                        .phoneNumber("010" + String.format("%08d", i))
                        .password("pw")
                        .role(USER)
                        .isDeleted(false)
                        .build()
                )
            );
        }

        List<UserTraining> list = new ArrayList<>();
        for (User u : users) {
            list.add(
                UserTraining.builder()
                    .programTraining(pt)
                    .status(UserTrainingStatus.PENDING)
                    .user(u)
                    .build()
            );
        }

        return list;
    }

    @Test
    void test_normal_save_individual() {
        ProgramTraining pt = setupTestData();
        List<UserTraining> list = buildUserTrainings(pt);

        long start = System.currentTimeMillis();

        for (UserTraining ut : list) {
            userTrainingRepository.save(ut);
        }

        long end = System.currentTimeMillis();
        System.out.println("====== 개별 INSERT 성능 ======");
        System.out.println("실행시간 : " + (end - start) + " ms");
        System.out.println("총 저장된 건수 = " + userTrainingRepository.count());
    }

    @Test
    void test_batch_insert_by_service() {
        ProgramTraining pt = setupTestData();
        List<User> users = new ArrayList<>();

        for (int i = 0; i < SIZE; i++) {
            users.add(
                userRepository.save(
                    User.builder()
                        .name("user" + i)
                        .email("u" + i + "@batch.com")
                        .phoneNumber("0109" + String.format("%07d", i))
                        .password("pw")
                        .role(USER)
                        .isDeleted(false)
                        .build()
                )
            );
        }

        long start = System.currentTimeMillis();

        assignmentService.assignForProgramTraining(pt, users);

        long end = System.currentTimeMillis();
        System.out.println("====== Batch INSERT 성능 ======");
        System.out.println("실행시간 : " + (end - start) + " ms");
        System.out.println("총 저장된 건수 = " + userTrainingRepository.count());
    }
}

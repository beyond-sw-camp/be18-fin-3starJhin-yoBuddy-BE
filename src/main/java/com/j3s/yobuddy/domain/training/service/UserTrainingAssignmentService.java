package com.j3s.yobuddy.domain.training.service;

import com.j3s.yobuddy.domain.training.entity.ProgramTraining;
import com.j3s.yobuddy.domain.training.entity.UserTraining;
import com.j3s.yobuddy.domain.training.entity.UserTrainingStatus;
import com.j3s.yobuddy.domain.training.repository.UserTrainingRepository;
import com.j3s.yobuddy.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserTrainingAssignmentService {

    private final UserTrainingRepository userTrainingRepository;

    @PersistenceContext
    private EntityManager em;

    private static final int BATCH_SIZE = 1000;

    @Transactional
    public void assignForUser(User user, List<ProgramTraining> pts) {

        int count = 0;

        for (ProgramTraining pt : pts) {
            UserTraining ut = UserTraining.builder()
                .user(user)
                .programTraining(pt)
                .status(UserTrainingStatus.PENDING)
                .build();

            em.persist(ut);

            if (++count % BATCH_SIZE == 0) {
                em.flush();
                em.clear();
            }
        }
    }

    @Transactional
    public void assignForProgramTraining(ProgramTraining pt, List<User> users) {

        int count = 0;

        for (User user : users) {
            UserTraining ut = UserTraining.builder()
                .user(user)
                .programTraining(pt)
                .status(UserTrainingStatus.PENDING)
                .build();

            em.persist(ut);

            if (++count % BATCH_SIZE == 0) {
                em.flush();
                em.clear();
            }
        }
    }
}

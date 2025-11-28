package com.j3s.yobuddy.domain.training.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.j3s.yobuddy.domain.training.entity.ProgramTraining;
import com.j3s.yobuddy.domain.training.entity.UserTraining;
import com.j3s.yobuddy.domain.training.entity.UserTrainingStatus;
import com.j3s.yobuddy.domain.training.repository.UserTrainingRepository;
import com.j3s.yobuddy.domain.user.entity.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserTrainingAssignmentService {

    private final UserTrainingRepository userTrainingRepository;

    @PersistenceContext
    private EntityManager em;

    private static final int BATCH_SIZE = 1000;

    @Transactional
    public void assignForUser(User user, List<ProgramTraining> pts) {

        int count = 0;

        for (ProgramTraining pt : pts) {
            boolean exists = userTrainingRepository
                .findByUser_UserIdAndProgramTraining_ProgramTrainingId(user.getUserId(), pt.getProgramTrainingId())
                .isPresent();

            if (exists) {
                continue;
            }

            UserTraining ut = UserTraining.builder()
                .user(user)
                .programTraining(pt)
                .status(UserTrainingStatus.PENDING)
                .build();

            try {
                em.persist(ut);
            } catch (DataIntegrityViolationException | PersistenceException ex) {
                log.warn("UserTraining insert conflict for userId={}, programTrainingId={} — skipping",
                    user.getUserId(), pt.getProgramTrainingId());
                continue;
            }

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
            boolean exists = userTrainingRepository
                .findByUser_UserIdAndProgramTraining_ProgramTrainingId(user.getUserId(), pt.getProgramTrainingId())
                .isPresent();

            if (exists) {
                continue;
            }

            UserTraining ut = UserTraining.builder()
                .user(user)
                .programTraining(pt)
                .status(UserTrainingStatus.PENDING)
                .build();

            try {
                em.persist(ut);
            } catch (DataIntegrityViolationException | PersistenceException ex) {
                log.warn("UserTraining insert conflict for userId={}, programTrainingId={} — skipping",
                    user.getUserId(), pt.getProgramTrainingId());
                continue;
            }

            if (++count % BATCH_SIZE == 0) {
                em.flush();
                em.clear();
            }
        }
    }
}

package com.j3s.yobuddy.domain.task.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.j3s.yobuddy.domain.task.entity.ProgramTask;
import com.j3s.yobuddy.domain.task.entity.UserTask;
import com.j3s.yobuddy.domain.task.repository.UserTaskRepository;
import com.j3s.yobuddy.domain.user.entity.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserTaskAssignmentService {

    private final UserTaskRepository userTaskRepository;

    @PersistenceContext
    private EntityManager em;

    private static final int BATCH_SIZE = 1000;

    @Transactional
    public void assignForUser(User user, List<ProgramTask> pts) {
        int count = 0;

        for (ProgramTask pt : pts) {
            boolean exists = userTaskRepository
                .findByUser_UserIdAndProgramTask_Id(user.getUserId(), pt.getId())
                .isPresent();

            if (exists) {
                continue;
            }

            UserTask ut = UserTask.builder()
                .user(user)
                .programTask(pt)
                .build();
            try {
                em.persist(ut);
            } catch (DataIntegrityViolationException | PersistenceException ex) {
                log.warn("UserTask insert conflict for userId={}, programTaskId={} — skipping",
                    user.getUserId(), pt.getId());
                continue;
            }

            if (++count % BATCH_SIZE == 0) {
                em.flush();
                em.clear();
            }
        }
    }

    @Transactional
    public void assignForProgramTask(ProgramTask pt, List<User> users) {
        int count = 0;

        for (User user : users) {
            boolean exists = userTaskRepository
                .findByUser_UserIdAndProgramTask_Id(user.getUserId(), pt.getId())
                .isPresent();

            if (exists) {
                continue;
            }

            UserTask ut = UserTask.builder()
                .user(user)
                .programTask(pt)
                .build();
            try {
                em.persist(ut);
            } catch (DataIntegrityViolationException | PersistenceException ex) {
                log.warn("UserTask insert conflict for userId={}, programTaskId={} — skipping",
                    user.getUserId(), pt.getId());
                continue;
            }

            if (++count % BATCH_SIZE == 0) {
                em.flush();
                em.clear();
            }
        }
    }
}

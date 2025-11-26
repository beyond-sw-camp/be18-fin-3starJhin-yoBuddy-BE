package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.task.entity.ProgramTask;
import com.j3s.yobuddy.domain.task.entity.UserTask;
import com.j3s.yobuddy.domain.task.repository.UserTaskRepository;
import com.j3s.yobuddy.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserTaskAssignmentService {

    private final UserTaskRepository userTaskRepository;

    @PersistenceContext
    private EntityManager em;

    private static final int BATCH_SIZE = 1000;

    @Transactional
    public void assignForUser(User user, List<ProgramTask> pts) {
        int count = 0;

        for (ProgramTask pt : pts) {
            UserTask ut = UserTask.builder()
                .user(user)
                .programTask(pt)
                .build();
            em.persist(ut);

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
            UserTask ut = UserTask.builder()
                .user(user)
                .programTask(pt)
                .build();
            em.persist(ut);

            if (++count % BATCH_SIZE == 0) {
                em.flush();
                em.clear();
            }
        }
    }
}

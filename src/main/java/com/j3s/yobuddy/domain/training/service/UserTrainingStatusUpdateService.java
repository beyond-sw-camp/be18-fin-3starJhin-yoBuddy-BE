package com.j3s.yobuddy.domain.training.service;

import com.j3s.yobuddy.domain.training.entity.UserTraining;
import com.j3s.yobuddy.domain.training.repository.UserTrainingRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserTrainingStatusUpdateService {

    private final UserTrainingRepository userTrainingRepository;

    @Transactional
    public int updateOverdueTrainings(LocalDate today) {

        LocalDate baseDate = (today != null) ? today : LocalDate.now();

        List<UserTraining> overdueList = userTrainingRepository.findOverdueTrainings(baseDate);

        log.info(
            "[UserTrainingStatusUpdate] today={}, overdueSize={}",
            baseDate,
            overdueList.size()
        );

        if (overdueList.isEmpty()) {
            return 0;
        }

        if (log.isDebugEnabled()) {
            overdueList.forEach(ut -> log.debug(
                "[UserTrainingStatusUpdate][BEFORE] utId={}, userId={}, trainingId={}, status={}, endDate={}",
                ut.getUserTrainingId(),
                ut.getUser().getUserId(),
                ut.getProgramTraining().getTraining().getTrainingId(),
                ut.getStatus(),
                ut.getProgramTraining().getEndDate()
            ));
        }

        overdueList.forEach(UserTraining::markMissed);

        if (log.isDebugEnabled()) {
            overdueList.forEach(ut -> log.debug(
                "[UserTrainingStatusUpdate][AFTER ] utId={}, userId={}, trainingId={}, status={}",
                ut.getUserTrainingId(),
                ut.getUser().getUserId(),
                ut.getProgramTraining().getTraining().getTrainingId(),
                ut.getStatus()
            ));
        }

        return overdueList.size();
    }

    /**
     * 매일 새벽 1시
     */
    @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
    public void scheduledUpdateOverdueTrainings() {
        LocalDate today = LocalDate.now();
        int updatedCount = updateOverdueTrainings(today);
        log.info("[UserTrainingStatusUpdate] scheduled run 완료 - today={}, updatedCount={}", today, updatedCount);
    }
}

package com.j3s.yobuddy.domain.risk.service;

import com.j3s.yobuddy.domain.kpi.results.repository.KpiResultsRepository;
import com.j3s.yobuddy.domain.mentor.mentoring.entity.MentoringStatus;
import com.j3s.yobuddy.domain.mentor.mentoring.repository.MentoringSessionRepository;
import com.j3s.yobuddy.domain.onboarding.entity.OnboardingProgram;
import com.j3s.yobuddy.domain.onboarding.repository.OnboardingProgramRepository;
import com.j3s.yobuddy.domain.task.entity.UserTaskStatus;
import com.j3s.yobuddy.domain.task.repository.UserTaskRepository;
import com.j3s.yobuddy.domain.user.entity.User;
import com.j3s.yobuddy.domain.user.repository.UserRepository;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport.WeeklyReportStatus;
import com.j3s.yobuddy.domain.weeklyReport.repository.WeeklyReportRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RiskMetricService {

    private final OnboardingProgramRepository onboardingProgramRepository;
    private final UserTaskRepository userTaskRepository;
    private final WeeklyReportRepository weeklyReportRepository;
    private final MentoringSessionRepository mentoringSessionRepository;
    private final KpiResultsRepository kpiResultsRepository;
    private final UserRepository userRepository;

    public double taskDelayRate(Long userId, Long programId) {
        int total = userTaskRepository.countTotalUserTasks(userId, programId);
        if (total == 0) return 0.0;

        int delayed = userTaskRepository.countUserTasksByStatus(
            userId,
            programId,
            List.of(UserTaskStatus.LATE, UserTaskStatus.MISSING)
        );
        return (double) delayed / total * 100;
    }

    public double mentoringAbsentRate(Long userId, Long programId) {
        long total =
            mentoringSessionRepository
                .countByMentee_UserIdAndProgram_ProgramIdAndDeletedFalse(
                    userId, programId
                );
        if (total == 0) return 0.0;

        long absent =
            mentoringSessionRepository
                .countByMentee_UserIdAndProgram_ProgramIdAndStatusInAndDeletedFalse(
                    userId,
                    programId,
                    List.of(
                        MentoringStatus.NO_SHOW
                    )
                );
        return (double) absent / total * 100;
    }

    public double weeklyReportMissingRate(Long userId, Long programId) {

        OnboardingProgram program =
            onboardingProgramRepository.findById(programId).orElseThrow();

        long totalWeeks =
            ChronoUnit.WEEKS.between(
                program.getStartDate(),
                program.getEndDate()
            ) + 1;

        long submitted =
            weeklyReportRepository
                .countByMenteeIdAndStatusInAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
                    userId,
                    List.of(
                        WeeklyReportStatus.SUBMITTED,
                        WeeklyReportStatus.REVIEWED
                    ),
                    program.getStartDate(),
                    program.getEndDate()
                );

        if (totalWeeks <= 0) return 0.0;
        return (double) (totalWeeks - submitted) / totalWeeks * 100;
    }

    public double kpiDeviation(Long userId, Long programId) {
        OnboardingProgram program =
            onboardingProgramRepository.findById(programId).orElseThrow();

        LocalDateTime start = program.getStartDate().atStartOfDay();
        LocalDateTime end = program.getEndDate().atTime(23, 59, 59);

        User user = userRepository.findById(userId).orElseThrow();
        Long deptId = user.getDepartment().getDepartmentId();

        Double userAvg =
            kpiResultsRepository.findUserAvgKpi(userId, start, end);
        Double deptAvg =
            kpiResultsRepository.findDeptAvgKpi(deptId, start, end);

        if (userAvg == null || deptAvg == null) return 0.0;
        return Math.abs(userAvg - deptAvg);
    }
}

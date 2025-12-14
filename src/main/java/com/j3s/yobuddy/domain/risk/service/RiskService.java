package com.j3s.yobuddy.domain.risk.service;

import com.j3s.yobuddy.domain.programenrollment.entity.ProgramEnrollment;
import com.j3s.yobuddy.domain.programenrollment.entity.ProgramEnrollment.EnrollmentStatus;
import com.j3s.yobuddy.domain.programenrollment.repository.ProgramEnrollmentRepository;
import com.j3s.yobuddy.domain.risk.constant.RiskLevel;
import com.j3s.yobuddy.domain.risk.dto.RiskDistributionResponse;

import com.j3s.yobuddy.domain.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RiskService {

    private final ProgramEnrollmentRepository enrollmentRepository;
    private final RiskCalculator riskCalculator;
    private final RiskMetricService riskMetricService;

    @Transactional(readOnly = true)
    public RiskDistributionResponse getRiskDistribution(Long programId) {

        int low = 0;
        int medium = 0;
        int high = 0;

        List<User> users =
            enrollmentRepository
                .findByProgram_ProgramIdAndStatus(programId, EnrollmentStatus.COMPLETED)
                .stream()
                .map(ProgramEnrollment::getUser)
                .toList();

        for (User user : users) {

            double taskDelayRate =
                riskMetricService.taskDelayRate(user.getUserId(), programId);

            double mentoringAbsentRate =
                riskMetricService.mentoringAbsentRate(user.getUserId(), programId);

            double weeklyReportMissingRate =
                riskMetricService.weeklyReportMissingRate(user.getUserId(), programId);

            double kpiDeviation =
                riskMetricService.kpiDeviation(user.getUserId(), programId);

            RiskLevel level =
                riskCalculator
                    .calculate(
                        taskDelayRate,
                        mentoringAbsentRate,
                        weeklyReportMissingRate,
                        kpiDeviation
                    )
                    .getLevel();

            switch (level) {
                case LOW -> low++;
                case MEDIUM -> medium++;
                case HIGH -> high++;
            }
        }

        return new RiskDistributionResponse(low, medium, high);
    }
}

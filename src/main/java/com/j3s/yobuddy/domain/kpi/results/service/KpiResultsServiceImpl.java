package com.j3s.yobuddy.domain.kpi.results.service;

import static com.j3s.yobuddy.domain.programenrollment.entity.ProgramEnrollment.EnrollmentStatus.ACTIVE;
import static com.j3s.yobuddy.domain.programenrollment.entity.ProgramEnrollment.EnrollmentStatus.COMPLETED;

import com.j3s.yobuddy.domain.kpi.goals.entity.KpiGoals;
import com.j3s.yobuddy.domain.kpi.goals.repository.KpiGoalsRepository;
import com.j3s.yobuddy.domain.kpi.results.dto.response.KpiResultsListResponse;
import com.j3s.yobuddy.domain.kpi.results.dto.response.KpiResultsResponse;
import com.j3s.yobuddy.domain.kpi.results.entity.KpiResults;
import com.j3s.yobuddy.domain.kpi.results.exception.KpiResultsNotFoundException;
import com.j3s.yobuddy.domain.kpi.results.repository.KpiResultsRepository;
import com.j3s.yobuddy.domain.onboarding.entity.OnboardingProgram;
import com.j3s.yobuddy.domain.onboarding.repository.OnboardingProgramRepository;
import com.j3s.yobuddy.domain.programenrollment.entity.ProgramEnrollment;
import com.j3s.yobuddy.domain.programenrollment.repository.ProgramEnrollmentRepository;
import com.j3s.yobuddy.domain.user.entity.User;
import com.j3s.yobuddy.domain.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KpiResultsServiceImpl implements KpiResultsService {

    private final KpiAggregationService aggregationService;

    private final KpiResultsRepository kpiResultsRepository;
    private final UserRepository userRepository;
    private final KpiGoalsRepository kpiGoalsRepository;
    private final OnboardingProgramRepository onboardingProgramRepository;
    private final ProgramEnrollmentRepository programEnrollmentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<KpiResultsListResponse> getResults(Long kpiGoalId, Long userId, Long departmentId) {
        List<KpiResults> result;
        if (kpiGoalId != null) {
            result = kpiResultsRepository.findByKpiGoalIdAndIsDeletedFalse(kpiGoalId);
        } else if (userId != null) {
            result = kpiResultsRepository.findByUserIdAndIsDeletedFalse(userId);
        } else if (departmentId != null) {
            result = kpiResultsRepository.findByDepartmentIdAndIsDeletedFalse(departmentId);
        } else {
            result = kpiResultsRepository.findAllByIsDeletedFalse();
        }
        return result.stream().map(KpiResultsListResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public KpiResultsResponse getResultById(Long kpiResultId) {
        KpiResults r = kpiResultsRepository.findByKpiResultIdAndIsDeletedFalse(kpiResultId)
            .orElseThrow(() -> new KpiResultsNotFoundException(kpiResultId));
        return KpiResultsResponse.from(r);
    }

    @Override
    public void culculateKpiResults() {
        calculateKpiResults(true, false);
    }

    @Override
    @Transactional
    public void calculateKpiResults(boolean includePastPrograms, boolean forceRecalculate) {

        List<OnboardingProgram> programs =
            onboardingProgramRepository.findByStatusAndDeletedFalse(
                OnboardingProgram.ProgramStatus.COMPLETED
            );

        for (OnboardingProgram program : programs) {
            if (program.isDeleted()) continue;

            Long programId = program.getProgramId();
            Long departmentId = program.getDepartment().getDepartmentId();

            LocalDateTime startDt = program.getStartDate().atStartOfDay();
            LocalDateTime endDt = program.getEndDate().atTime(23, 59, 59);

            long expectedWeeks =
                Math.max(
                    1,
                    ChronoUnit.WEEKS.between(
                        program.getStartDate(),
                        program.getEndDate()
                    ) + 1
                );

            KpiAggregatedResult aggregated =
                aggregationService.aggregate(programId, startDt, endDt);

            List<KpiGoals> goals =
                kpiGoalsRepository.findByDepartmentIdAndIsDeletedFalse(departmentId);

            List<ProgramEnrollment> enrollments =
                programEnrollmentRepository.findByProgram_ProgramIdAndStatusIn(
                    programId,
                    List.of(ACTIVE, COMPLETED)
                );

            List<KpiResults> batch = new ArrayList<>();

            for (ProgramEnrollment e : enrollments) {
                User user = e.getUser();

                for (KpiGoals goal : goals) {
                    if (goal.getKpiCategoryId().equals(6L)) {
                        continue;
                    }
                    BigDecimal achieved =
                        aggregated.get(user.getUserId(), goal.getKpiCategoryId());

                    batch.add(
                        KpiResults.builder()
                            .userId(user.getUserId())
                            .departmentId(departmentId)
                            .kpiGoalId(goal.getKpiGoalId())
                            .achievedValue(achieved)
                            .evaluatedAt(endDt)
                            .isDeleted(false)
                            .build()
                    );
                }
            }

            kpiResultsRepository.saveAll(batch);
        }
    }
}

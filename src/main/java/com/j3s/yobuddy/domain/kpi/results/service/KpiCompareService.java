package com.j3s.yobuddy.domain.kpi.results.service;

import com.j3s.yobuddy.domain.kpi.goals.entity.KpiGoals;
import com.j3s.yobuddy.domain.kpi.goals.repository.KpiGoalsRepository;
import com.j3s.yobuddy.domain.kpi.results.dto.response.KpiCompareResponse;
import com.j3s.yobuddy.domain.kpi.results.entity.KpiResults;
import com.j3s.yobuddy.domain.kpi.results.repository.DeptKpiAvgProjection;
import com.j3s.yobuddy.domain.kpi.results.repository.KpiResultsRepository;
import com.j3s.yobuddy.domain.user.entity.User;
import com.j3s.yobuddy.domain.user.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KpiCompareService {

    private final UserRepository userRepository;
    private final KpiResultsRepository kpiResultsRepository;
    private final KpiGoalsRepository kpiGoalsRepository;

    @Transactional(readOnly = true)
    public KpiCompareResponse compare(Long userId) {

        User user = userRepository.findById(userId)
            .orElseThrow();

        Long departmentId = user.getDepartment().getDepartmentId();

        // 1️⃣ 부서 KPI Goal 목록
        List<KpiGoals> goals =
            kpiGoalsRepository.findByDepartmentIdAndIsDeletedFalse(departmentId);

        // 2️⃣ 사용자 KPI (goal별 최신)
        List<KpiResults> userResults =
            kpiResultsRepository.findByUserIdAndIsDeletedFalse(userId);

        var userResultMap =
            userResults.stream()
                .collect(Collectors.toMap(
                    KpiResults::getKpiGoalId,
                    KpiResults::getAchievedValue,
                    (a, b) -> a // 최신 1건만 쓰는 구조라 충돌 없음
                ));

        // 3️⃣ 부서 평균 KPI
        var deptAvgMap =
            kpiResultsRepository.findDeptAvgKpi(departmentId)
                .stream()
                .collect(Collectors.toMap(
                    DeptKpiAvgProjection::getKpiGoalId,
                    DeptKpiAvgProjection::getAvgScore
                ));

        // 4️⃣ 비교 아이템 구성
        List<KpiCompareResponse.Item> items =
            goals.stream()
                .map(goal ->
                    KpiCompareResponse.Item.builder()
                        .kpiGoalId(goal.getKpiGoalId())
                        .kpiName(goal.getDescription())
                        .userScore(
                            userResultMap.getOrDefault(
                                goal.getKpiGoalId(),
                                BigDecimal.ZERO
                            )
                        )
                        .departmentAvgScore(
                            deptAvgMap.getOrDefault(
                                goal.getKpiGoalId(),
                                BigDecimal.ZERO
                            )
                        )
                        .build()
                )
                .toList();

        return KpiCompareResponse.builder()
            .user(
                KpiCompareResponse.UserInfo.builder()
                    .userId(user.getUserId())
                    .name(user.getName())
                    .build()
            )
            .department(
                KpiCompareResponse.DepartmentInfo.builder()
                    .departmentId(departmentId)
                    .name(user.getDepartment().getName())
                    .build()
            )
            .items(items)
            .build();
    }
}

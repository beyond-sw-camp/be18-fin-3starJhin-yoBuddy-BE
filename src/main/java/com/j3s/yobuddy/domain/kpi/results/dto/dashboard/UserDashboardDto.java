package com.j3s.yobuddy.domain.kpi.results.dto.dashboard;

import com.j3s.yobuddy.domain.user.entity.User;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDashboardDto {

    private Long userId;
    private String name;
    private String position;

    private BigDecimal totalScore;
    private boolean pass;

    private List<UserKpiResultDto> results;

    public static UserDashboardDto of(User u, BigDecimal totalScore, boolean pass, List<UserKpiResultDto> resultDtos) {
        return UserDashboardDto.builder()
            .userId(u.getUserId())
            .name(u.getName())
            .totalScore(totalScore)
            .pass(pass)
            .results(resultDtos)
            .build();
    }
}

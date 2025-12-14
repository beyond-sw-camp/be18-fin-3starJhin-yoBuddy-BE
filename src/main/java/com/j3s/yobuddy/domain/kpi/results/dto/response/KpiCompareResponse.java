package com.j3s.yobuddy.domain.kpi.results.dto.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KpiCompareResponse {

    private UserInfo user;
    private DepartmentInfo department;
    private List<Item> items;
    private Summary summary;

    @Getter @Builder
    public static class UserInfo {
        private Long userId;
        private String name;
    }

    @Getter @Builder
    public static class DepartmentInfo {
        private Long departmentId;
        private String name;
    }

    @Getter @Builder
    public static class Item {
        private Long kpiGoalId;
        private String kpiName;
        private BigDecimal userScore;
        private BigDecimal departmentAvgScore;
    }

    @Getter @Builder
    public static class Summary {
        private BigDecimal userTotal;
        private BigDecimal departmentTotalAvg;
    }
}

package com.j3s.yobuddy.domain.kpi.results.dto.dashboard;

import java.util.List;
import lombok.Getter;

@Getter
public class RadarDepartmentScoreDto {

    private Long departmentId;
    private String departmentName;
    private List<RadarPointDto> points;

    public RadarDepartmentScoreDto(Long departmentId, String departmentName, List<RadarPointDto> points) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.points = points;
    }
}
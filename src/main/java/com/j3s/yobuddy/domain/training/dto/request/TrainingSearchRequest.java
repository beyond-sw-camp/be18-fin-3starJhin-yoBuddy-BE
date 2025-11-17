package com.j3s.yobuddy.domain.training.dto.request;

import com.j3s.yobuddy.domain.training.entity.TrainingType;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class TrainingSearchRequest {
    private TrainingType type;
    private Long programId;
    private String keyword;
    private Integer page;
    private Integer size;
}

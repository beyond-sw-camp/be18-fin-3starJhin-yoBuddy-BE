package com.j3s.yobuddy.domain.formresult.dto.response;

import com.j3s.yobuddy.domain.formresult.entity.FormResult;
import com.j3s.yobuddy.domain.formresult.entity.FormResultStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class FormResultResponse {

    private final Long formResultId;
    private final Long programTrainingId;
    private final String userName;
    private final String departmentName;
    private final String trainingName;
    private final String programName;
    private final BigDecimal score;
    private final BigDecimal maxScore;
    private final BigDecimal passingScore;
    private final FormResultStatus result;
    private final LocalDateTime submittedAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static FormResultResponse from(FormResult fr) {
        return FormResultResponse.builder()
            .formResultId(fr.getFormResultId())
            .programTrainingId(fr.getProgramTraining().getProgramTrainingId())
            .userName(fr.getUser().getName())
            .departmentName(fr.getUser().getDepartment().getName())
            .trainingName(fr.getProgramTraining().getTraining().getTitle())
            .programName(fr.getProgramTraining().getProgram().getName())
            .score(fr.getScore())
            .maxScore(fr.getMaxScore())
            .passingScore(fr.getPassingScore())
            .result(fr.getResult())
            .submittedAt(fr.getSubmittedAt())
            .createdAt(fr.getCreatedAt())
            .updatedAt(fr.getUpdatedAt())
            .build();
    }
}

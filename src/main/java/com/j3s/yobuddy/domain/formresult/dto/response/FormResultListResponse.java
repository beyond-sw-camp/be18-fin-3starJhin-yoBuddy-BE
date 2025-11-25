package com.j3s.yobuddy.domain.formresult.dto.response;

import com.j3s.yobuddy.domain.formresult.entity.FormResult;
import com.j3s.yobuddy.domain.formresult.entity.FormResultStatus;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class FormResultListResponse {

    private final Long formResultId;
    private final String userName;
    private final String trainingName;
    private final String programName;
    private final BigDecimal score;
    private final FormResultStatus result;

    public static FormResultResponse from(FormResult fr) {
        return FormResultResponse.builder()
            .formResultId(fr.getFormResultId())
            .userName(fr.getUser().getName())
            .trainingName(fr.getTraining().getTitle())
            .programName(fr.getOnboardingProgram().getName())
            .score(fr.getScore())
            .result(fr.getResult())
            .build();
    }

}

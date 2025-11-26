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

    public static FormResultListResponse from(FormResult fr) {
        return FormResultListResponse.builder()
            .formResultId(fr.getFormResultId())
            .userName(fr.getUser().getName())
            .trainingName(fr.getProgramTraining().getTraining().getTitle())
            .programName(fr.getProgramTraining().getProgram().getName())
            .score(fr.getScore())
            .result(fr.getResult())
            .build();
    }

}

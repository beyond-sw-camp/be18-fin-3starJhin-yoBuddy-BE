package com.j3s.yobuddy.domain.formresult.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class FormResultResponse {

    private final Long formResultId;
    private final Long userName;

}

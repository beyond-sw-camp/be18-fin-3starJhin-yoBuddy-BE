package com.j3s.yobuddy.domain.formresult.dto.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class FormResultUpdateRequest {

    private final BigDecimal score;
}

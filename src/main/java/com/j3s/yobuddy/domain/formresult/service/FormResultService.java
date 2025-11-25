package com.j3s.yobuddy.domain.formresult.service;

import com.j3s.yobuddy.domain.formresult.dto.request.FormResultCreateRequest;
import com.j3s.yobuddy.domain.formresult.entity.FormResult;
import jakarta.validation.Valid;

public interface FormResultService {

    FormResult createFormResult(@Valid FormResultCreateRequest request);
}

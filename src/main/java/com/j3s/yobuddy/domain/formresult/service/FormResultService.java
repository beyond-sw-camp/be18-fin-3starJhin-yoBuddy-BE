package com.j3s.yobuddy.domain.formresult.service;

import com.j3s.yobuddy.domain.formresult.dto.request.FormResultCreateRequest;
import com.j3s.yobuddy.domain.formresult.dto.response.FormResultListResponse;
import com.j3s.yobuddy.domain.formresult.entity.FormResult;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FormResultService {

    FormResult createFormResult(@Valid FormResultCreateRequest request);

    void deleteFormResult(Long formResultId);

    Page<FormResultListResponse> getFormResultList(Pageable pageable);
}

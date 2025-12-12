package com.j3s.yobuddy.api.common;

import com.j3s.yobuddy.domain.formresult.dto.request.FormResultCreateRequest;
import com.j3s.yobuddy.domain.formresult.dto.response.FormResultResponse;
import com.j3s.yobuddy.domain.formresult.entity.FormResult;
import com.j3s.yobuddy.domain.formresult.service.FormResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/webhook/trainings/results")
public class WebhookController {

    private final FormResultService formResultService;

    @PostMapping
    public ResponseEntity<FormResultResponse> createFormResult(
        @Valid @RequestBody FormResultCreateRequest request) {

        FormResult formResult = formResultService.createFormResult(request);
        FormResultResponse result = FormResultResponse.from(formResult);

        return ResponseEntity.ok(result);
    }
}

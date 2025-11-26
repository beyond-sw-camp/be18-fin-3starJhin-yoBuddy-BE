package com.j3s.yobuddy.api.admin;

import com.j3s.yobuddy.domain.formresult.dto.request.FormResultCreateRequest;
import com.j3s.yobuddy.domain.formresult.dto.response.FormResultListResponse;
import com.j3s.yobuddy.domain.formresult.dto.response.FormResultResponse;
import com.j3s.yobuddy.domain.formresult.entity.FormResult;
import com.j3s.yobuddy.domain.formresult.service.FormResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin/trainings/results")
public class AdminFormResultController {

    private final FormResultService formResultService;

    @PostMapping
    public ResponseEntity<FormResultResponse> createFormResult(
        @Valid @RequestBody FormResultCreateRequest request) {

        FormResult formResult = formResultService.createFormResult(request);
        FormResultResponse result = FormResultResponse.from(formResult);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{formResultId}")
    public ResponseEntity<Void> deleteFormResult(@PathVariable Long formResultId) {

        formResultService.deleteFormResult(formResultId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<FormResultListResponse>> getFormResultList(
        @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<FormResultListResponse> formResults = formResultService.getFormResultList(pageable);

        return ResponseEntity.ok(formResults);
    }

}

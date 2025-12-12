package com.j3s.yobuddy.domain.formresult.repository;

import com.j3s.yobuddy.domain.formresult.entity.FormResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FormResultRepositoryCustom {

    Page<FormResult> searchFormResults(
        String trainingName,
        String onboardingName,
        String userName,
        Pageable pageable
    );

}

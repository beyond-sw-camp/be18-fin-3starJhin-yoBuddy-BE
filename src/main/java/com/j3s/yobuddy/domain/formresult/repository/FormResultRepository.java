package com.j3s.yobuddy.domain.formresult.repository;

import com.j3s.yobuddy.domain.formresult.entity.FormResult;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormResultRepository extends JpaRepository<FormResult, Long> {

    Optional<FormResult> findByFormResultIdAndIsDeletedFalse(Long formResultId);
}

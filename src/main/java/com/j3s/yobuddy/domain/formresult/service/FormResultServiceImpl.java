package com.j3s.yobuddy.domain.formresult.service;

import com.j3s.yobuddy.domain.formresult.dto.request.FormResultCreateRequest;
import com.j3s.yobuddy.domain.formresult.dto.response.FormResultListResponse;
import com.j3s.yobuddy.domain.formresult.entity.FormResult;
import com.j3s.yobuddy.domain.formresult.entity.FormResultStatus;
import com.j3s.yobuddy.domain.formresult.exception.FormResultAlreadyDeletedException;
import com.j3s.yobuddy.domain.formresult.exception.FormResultNotFoundException;
import com.j3s.yobuddy.domain.formresult.repository.FormResultRepository;
import com.j3s.yobuddy.domain.onboarding.entity.OnboardingProgram;
import com.j3s.yobuddy.domain.onboarding.exception.ProgramNotFoundException;
import com.j3s.yobuddy.domain.onboarding.repository.OnboardingProgramRepository;
import com.j3s.yobuddy.domain.training.entity.ProgramTraining;
import com.j3s.yobuddy.domain.training.entity.Training;
import com.j3s.yobuddy.domain.training.entity.UserTraining;
import com.j3s.yobuddy.domain.training.exception.ProgramTrainingNotFoundException;
import com.j3s.yobuddy.domain.training.exception.TrainingNotFoundException;
import com.j3s.yobuddy.domain.training.exception.UserTrainingsNotFoundException;
import com.j3s.yobuddy.domain.training.repository.ProgramTrainingRepository;
import com.j3s.yobuddy.domain.training.repository.TrainingRepository;
import com.j3s.yobuddy.domain.training.repository.UserTrainingRepository;
import com.j3s.yobuddy.domain.user.entity.User;
import com.j3s.yobuddy.domain.user.exception.UserNotFoundException;
import com.j3s.yobuddy.domain.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FormResultServiceImpl implements FormResultService {

    private final FormResultRepository formResultRepository;
    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;
    private final OnboardingProgramRepository onboardingProgramRepository;
    private final ProgramTrainingRepository programTrainingRepository;
    private final UserTrainingRepository userTrainingRepository;

    @Override
    @Transactional
    public FormResult createFormResult(FormResultCreateRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));

        OnboardingProgram onboardingProgram = onboardingProgramRepository
            .findByName(request.getOnboardingName())
            .orElseThrow(() -> new ProgramNotFoundException("온보딩 프로그램을 찾을 수 없습니다."));

        Training training = trainingRepository
            .findByTitle(request.getTrainingName())
            .orElseThrow(() -> new TrainingNotFoundException("교육 프로그램을 찾을 수 없습니다."));

        Long programId = onboardingProgram.getProgramId();

        Long trainingId = training.getTrainingId();

        ProgramTraining programTraining = programTrainingRepository.findByProgram_ProgramIdAndTraining_TrainingId(
                programId, trainingId)
            .orElseThrow(() -> new ProgramTrainingNotFoundException(programId, trainingId));

        FormResultStatus resultStatus =
            request.getScore().compareTo(request.getPassingScore()) >= 0
                ? FormResultStatus.PASS
                : FormResultStatus.FAIL;

        FormResult formResult = FormResult.builder()
            .score(request.getScore())
            .maxScore(request.getMaxScore())
            .passingScore(request.getPassingScore())
            .result(resultStatus)
            .programTraining(programTraining)
            .submittedAt(request.getSubmittedAt())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .isDeleted(false)
            .user(user)
            .build();

        formResultRepository.save(formResult);

        Long userId = user.getUserId();

        Long programTrainingId = programTraining.getProgramTrainingId();

        UserTraining userTraining = userTrainingRepository.findByUser_UserIdAndProgramTraining_ProgramTrainingId(
                userId, programTrainingId)
            .orElseThrow(() -> new UserTrainingsNotFoundException(userId));

        BigDecimal score = formResult.getScore();

        FormResultStatus result = formResult.getResult();

        UserTraining updatedUserTraining = userTraining.toBuilder()
            .score(score)
            .result(result)
            .updatedAt(LocalDateTime.now())
            .build();

        userTrainingRepository.save(updatedUserTraining);

        return formResult;
    }

    @Override
    @Transactional
    public void deleteFormResult(Long formResultId) {

        FormResult formResult = formResultRepository.findByFormResultIdAndIsDeletedFalse(
            formResultId).orElseThrow(() -> new FormResultNotFoundException(formResultId));

        if (formResult.getIsDeleted()) {
            throw new FormResultAlreadyDeletedException(formResultId);
        }

        formResult.softDelete();
        formResultRepository.save(formResult);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FormResultListResponse> getFormResultList(Pageable pageable) {

        Page<FormResult> result;

        result = formResultRepository.findAllByIsDeletedFalse(pageable);

        return result.map(FormResultListResponse::from);
    }
}

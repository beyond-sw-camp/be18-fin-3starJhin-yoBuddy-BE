package com.j3s.yobuddy.domain.formresult.service;

import com.j3s.yobuddy.domain.formresult.dto.request.FormResultCreateRequest;
import com.j3s.yobuddy.domain.formresult.entity.FormResult;
import com.j3s.yobuddy.domain.formresult.entity.FormResultStatus;
import com.j3s.yobuddy.domain.formresult.repository.FormResultRepository;
import com.j3s.yobuddy.domain.onboarding.entity.OnboardingProgram;
import com.j3s.yobuddy.domain.onboarding.exception.ProgramNotFoundException;
import com.j3s.yobuddy.domain.onboarding.repository.OnboardingProgramRepository;
import com.j3s.yobuddy.domain.training.entity.Training;
import com.j3s.yobuddy.domain.training.exception.TrainingNotFoundException;
import com.j3s.yobuddy.domain.training.repository.TrainingRepository;
import com.j3s.yobuddy.domain.user.entity.User;
import com.j3s.yobuddy.domain.user.exception.UserNotFoundException;
import com.j3s.yobuddy.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FormResultServiceImpl implements FormResultService {

    private final FormResultRepository formResultRepository;
    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;
    private final OnboardingProgramRepository onboardingProgramRepository;

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

        FormResultStatus resultStatus =
            request.getScore().compareTo(request.getPassingScore()) >= 0
                ? FormResultStatus.PASS
                : FormResultStatus.FAIL;

        FormResult formResult = FormResult.builder()
            .score(request.getScore())
            .maxScore(request.getMaxScore())
            .passingScore(request.getPassingScore())
            .result(resultStatus)
            .submittedAt(request.getSubmittedAt())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .isDeleted(false)
            .user(user)
            .training(training)
            .onboardingProgram(onboardingProgram)
            .build();

        return formResultRepository.save(formResult);
    }
}

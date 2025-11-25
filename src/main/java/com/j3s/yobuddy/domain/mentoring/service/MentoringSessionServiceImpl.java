package com.j3s.yobuddy.domain.mentoring.service;

import com.j3s.yobuddy.domain.mentor.exception.MentorNotFoundException;
import com.j3s.yobuddy.domain.mentoring.dto.request.MentoringSessionCreateRequest;
import com.j3s.yobuddy.domain.mentoring.dto.request.MentoringSessionUpdateRequest;
import com.j3s.yobuddy.domain.mentoring.dto.response.MentoringSessionResponse;
import com.j3s.yobuddy.domain.mentoring.entity.MentoringSession;
import com.j3s.yobuddy.domain.mentoring.entity.MentoringStatus;
import com.j3s.yobuddy.domain.mentoring.exception.MenteeNotFoundException;
import com.j3s.yobuddy.domain.mentoring.exception.MentoringSessionNotFoundException;
import com.j3s.yobuddy.domain.mentoring.repository.MentoringSessionRepository;
import com.j3s.yobuddy.domain.notification.entity.NotificationType;
import com.j3s.yobuddy.domain.notification.service.NotificationService;
import com.j3s.yobuddy.domain.programenrollment.entity.ProgramEnrollment;
import com.j3s.yobuddy.domain.programenrollment.entity.ProgramEnrollment.EnrollmentStatus;
import com.j3s.yobuddy.domain.programenrollment.exception.ProgramEnrollmentNotFoundException;
import com.j3s.yobuddy.domain.programenrollment.repository.ProgramEnrollmentRepository;
import com.j3s.yobuddy.domain.user.entity.User;
import com.j3s.yobuddy.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MentoringSessionServiceImpl implements MentoringSessionService {

    private final MentoringSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final ProgramEnrollmentRepository enrollmentRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public MentoringSessionResponse create(MentoringSessionCreateRequest req) {

        User mentor = userRepository.findById(req.getMentorId())
            .orElseThrow(() -> new MentorNotFoundException(req.getMentorId()));

        User mentee = userRepository.findById(req.getMenteeId())
            .orElseThrow(() -> new MenteeNotFoundException(req.getMenteeId()));

        ProgramEnrollment enrollment = enrollmentRepository
            .findByUser_UserIdAndStatus(mentee.getUserId(), EnrollmentStatus.ACTIVE)
            .orElseThrow(() -> new ProgramEnrollmentNotFoundException(mentee.getUserId()));

        MentoringSession session = MentoringSession.builder()
            .mentor(mentor)
            .mentee(mentee)
            .program(enrollment.getProgram())
            .description(req.getDescription())
            .scheduledAt(req.getScheduledAt())
            .status(MentoringStatus.SCHEDULED)
            .feedback(null)
            .deleted(false)
            .build();

        sessionRepository.save(session);

        notificationService.notify(
            mentee,
            NotificationType.MENTORING,
            "새로운 멘토링 세션이 생성되었습니다",
            "세션 일정: " + session.getScheduledAt()
        );

        return toResponse(session);
    }

    @Override
    @Transactional(readOnly = true)
    public MentoringSessionResponse get(Long id) {
        MentoringSession session = sessionRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new MentoringSessionNotFoundException(id));

        return toResponse(session);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentoringSessionResponse> getByMentor(Long mentorId) {
        return sessionRepository.findByMentor_UserIdAndDeletedFalse(mentorId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentoringSessionResponse> getByMentee(Long menteeId) {
        return sessionRepository.findByMentee_UserIdAndDeletedFalse(menteeId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentoringSessionResponse> getByProgram(Long programId) {
        return sessionRepository.findByProgram_ProgramIdAndDeletedFalse(programId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    @Transactional
    public MentoringSessionResponse update(Long id, MentoringSessionUpdateRequest req) {

        MentoringSession session = sessionRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new MentoringSessionNotFoundException(id));

        LocalDateTime schedule = req.getScheduledAt() != null
            ? LocalDateTime.parse(req.getScheduledAt())
            : null;

        MentoringStatus status = null;
        if (req.getStatus() != null) {
            status = MentoringStatus.valueOf(String.valueOf(req.getStatus()));
        }

        session.update(
            schedule,
            status,
            req.getFeedback(),
            req.getDescription()
        );
        return toResponse(session);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        MentoringSession session = sessionRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new MentoringSessionNotFoundException(id));

        session.softDelete();
    }

    private MentoringSessionResponse toResponse(MentoringSession s) {
        User mentee = s.getMentee();

        return MentoringSessionResponse.builder()
            .id(s.getId())
            .mentorId(s.getMentor().getUserId())
            .menteeId(mentee.getUserId())
            .programId(s.getProgram().getProgramId())
            .mentorName(s.getMentor().getName())
            .menteeName(mentee.getName())
            .menteeEmail(mentee.getEmail())
            .menteePhoneNumber(mentee.getPhoneNumber())
            .scheduledAt(s.getScheduledAt())
            .description(s.getDescription())
            .status(s.getStatus())
            .feedback(s.getFeedback())
            .createdAt(s.getCreatedAt())
            .updatedAt(s.getUpdatedAt())
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentoringSessionResponse> getAll() {
        return sessionRepository.findAllByDeletedFalse()
            .stream()
            .map(this::toResponse)
            .toList();
    }
}

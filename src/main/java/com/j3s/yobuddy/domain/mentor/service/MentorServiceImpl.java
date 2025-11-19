package com.j3s.yobuddy.domain.mentor.service;

import com.j3s.yobuddy.domain.mentor.dto.request.AssignMenteeRequest;
import com.j3s.yobuddy.domain.mentor.dto.response.MenteeCandidateResponse;
import com.j3s.yobuddy.domain.mentor.dto.response.MenteeDetailResponse;
import com.j3s.yobuddy.domain.mentor.dto.response.MenteeListResponse;
import com.j3s.yobuddy.domain.mentor.entity.MentorMenteeAssignment;
import com.j3s.yobuddy.domain.mentor.exception.AssignmentNotFoundException;
import com.j3s.yobuddy.domain.mentor.exception.InvalidMenteeRoleException;
import com.j3s.yobuddy.domain.mentor.exception.MenteeAlreadyAssignedException;
import com.j3s.yobuddy.domain.mentor.exception.MentorNotFoundException;
import com.j3s.yobuddy.domain.mentor.exception.UnauthorizedMenteeAccessException;
import com.j3s.yobuddy.domain.mentor.repository.MentorMenteeAssignmentRepository;
import com.j3s.yobuddy.domain.user.entity.Role;
import com.j3s.yobuddy.domain.user.entity.User;
import com.j3s.yobuddy.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MentorServiceImpl implements MentorService {

    private final UserRepository userRepository;
    private final MentorMenteeAssignmentRepository assignmentRepository;

    @Override
    @Transactional
    public void assignMentee(Long mentorId, AssignMenteeRequest request) {

        User mentor = userRepository.findById(mentorId)
            .filter(u -> u.getRole() == Role.MENTOR)
            .orElseThrow(() -> new MentorNotFoundException(mentorId));

        Long deptId = mentor.getDepartment().getDepartmentId();

        for (Long menteeId : request.getMenteeIds()) {

            User mentee = userRepository.findById(menteeId)
                .orElseThrow(() -> new AssignmentNotFoundException(menteeId));

            if (mentee.getRole() != Role.USER) {
                throw new InvalidMenteeRoleException(mentee.getUserId(), mentee.getRole());
            }

            if (!mentee.getDepartment().getDepartmentId().equals(deptId)) {
                throw new UnauthorizedMenteeAccessException(mentorId, menteeId);
            }

            if (assignmentRepository.existsByMenteeUserIdAndDeletedFalse(mentee.getUserId())) {
                throw new MenteeAlreadyAssignedException(mentee.getUserId());
            }

            MentorMenteeAssignment assignment = MentorMenteeAssignment.builder()
                .mentor(mentor)
                .mentee(mentee)
                .deleted(false)
                .build();

            assignmentRepository.save(assignment);
        }
    }

    @Override
    @Transactional
    public void removeMentee(Long mentorId, Long menteeId) {

        MentorMenteeAssignment assignment = assignmentRepository
            .findByMenteeUserIdAndDeletedFalse(menteeId)
            .orElseThrow(() -> new AssignmentNotFoundException(menteeId));

        if (!assignment.getMentor().getUserId().equals(mentorId)) {
            throw new UnauthorizedMenteeAccessException(mentorId, menteeId);
        }

        assignment.softDelete();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenteeListResponse> getMentees(Long mentorId) {

        List<MentorMenteeAssignment> assignments =
            assignmentRepository.findByMentorUserIdAndDeletedFalse(mentorId);

        return assignments.stream()
            .map(a -> new MenteeListResponse(
                a.getMentee().getUserId(),
                a.getMentee().getName(),
                a.getMentee().getEmail(),
                a.getMentee().getPhoneNumber(),
                a.getMentee().getDepartment().getName()
            ))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenteeCandidateResponse> getDepartmentNewbies(Long mentorId) {

        User mentor = userRepository.findById(mentorId)
            .filter(u -> u.getRole() == Role.MENTOR)
            .orElseThrow(() -> new MentorNotFoundException(mentorId));

        Long deptId = mentor.getDepartment().getDepartmentId();

        List<User> newbies =
            userRepository.findByDepartment_DepartmentIdAndRole(deptId, Role.USER);

        return newbies.stream()
            .map(u -> new MenteeCandidateResponse(
                u.getUserId(),
                u.getName(),
                u.getEmail(),
                u.getDepartment().getName()
            ))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MenteeDetailResponse getMenteeDetail(Long mentorId, Long menteeId) {

        // 멘티가 실제로 mentorId에게 배정된 상태인지 확인
        MentorMenteeAssignment assignment = assignmentRepository
            .findByMenteeUserIdAndDeletedFalse(menteeId)
            .orElseThrow(() -> new AssignmentNotFoundException(menteeId));

        if (!assignment.getMentor().getUserId().equals(mentorId)) {
            throw new UnauthorizedMenteeAccessException(mentorId, menteeId);
        }

        User mentee = assignment.getMentee();

        return new MenteeDetailResponse(
            mentee.getUserId(),
            mentee.getName(),
            mentee.getEmail(),
            mentee.getPhoneNumber(),
            mentee.getDepartment().getName(),
            mentee.getJoinedAt() != null ? mentee.getJoinedAt().toString() : null
        );
    }
}

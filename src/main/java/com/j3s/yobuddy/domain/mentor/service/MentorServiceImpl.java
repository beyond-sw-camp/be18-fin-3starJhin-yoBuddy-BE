package com.j3s.yobuddy.domain.mentor.service;

import com.j3s.yobuddy.domain.mentor.dto.request.AssignMenteeRequest;
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

        User mentee = userRepository.findById(request.getMenteeId())
            .orElseThrow(() -> new AssignmentNotFoundException(request.getMenteeId()));

        if (mentee.getRole() != Role.USER) {
            throw new InvalidMenteeRoleException(mentee.getUserId(), mentee.getRole());
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
}

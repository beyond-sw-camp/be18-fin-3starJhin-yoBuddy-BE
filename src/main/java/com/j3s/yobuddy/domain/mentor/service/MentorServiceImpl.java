package com.j3s.yobuddy.domain.mentor.service;

import com.j3s.yobuddy.domain.mentor.dto.request.AssignMenteeRequest;
import com.j3s.yobuddy.domain.mentor.dto.response.MenteeListResponse;
import com.j3s.yobuddy.domain.mentor.entity.Mentor;
import com.j3s.yobuddy.domain.mentor.entity.MentorMenteeAssignment;
import com.j3s.yobuddy.domain.mentor.exception.AssignmentNotFoundException;
import com.j3s.yobuddy.domain.mentor.exception.MenteeAlreadyAssignedException;
import com.j3s.yobuddy.domain.mentor.exception.MentorNotFoundException;
import com.j3s.yobuddy.domain.mentor.exception.UnauthorizedMenteeAccessException;
import com.j3s.yobuddy.domain.mentor.repository.MentorMenteeAssignmentRepository;
import com.j3s.yobuddy.domain.mentor.repository.MentorRepository;
import com.j3s.yobuddy.domain.user.entity.User;
import com.j3s.yobuddy.domain.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MentorServiceImpl implements MentorService {

    private final MentorRepository mentorRepository;
    private final MentorMenteeAssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void assignMentee(Long mentorId, AssignMenteeRequest request) {

        Mentor mentor = mentorRepository.findById(mentorId)
            .orElseThrow(() -> new MentorNotFoundException(mentorId));

        User mentee = userRepository.findById(request.getMenteeId())
            .orElseThrow(() -> new AssignmentNotFoundException(
                request.getMenteeId()));

        if (assignmentRepository.existsByMenteeUserIdAndDeletedFalse(mentee.getUserId())) {
            throw new MenteeAlreadyAssignedException(mentee.getUserId());
        }

        MentorMenteeAssignment assignment = MentorMenteeAssignment.builder()
            .mentor(mentor)
            .mentee(mentee)
            .deleted(false)
            .build();

        mentor.addMentee(assignment);

        assignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public void removeMentee(Long mentorId, Long menteeId) {

        MentorMenteeAssignment assignment = assignmentRepository
            .findByMenteeUserIdAndDeletedFalse(menteeId)
            .orElseThrow(() -> new AssignmentNotFoundException(menteeId));

        if (!assignment.getMentor()
            .getMentorId()
            .equals(mentorId)) {
            throw new UnauthorizedMenteeAccessException(mentorId, menteeId);
        }

        assignment.deleteAssignment();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenteeListResponse> getMentees(Long mentorId) {

        List<MentorMenteeAssignment> assignments =
            assignmentRepository.findByMentorMentorIdAndDeletedFalse(mentorId);

        return assignments.stream()
            .map(a -> new MenteeListResponse(
                a.getMentee()
                    .getUserId(),
                a.getMentee()
                    .getName(),
                a.getMentee()
                    .getEmail(),
                a.getMentee()
                    .getPhoneNumber(),
                a.getMentee()
                    .getDepartment()
                    .getName()
            ))
            .collect(Collectors.toList());
    }
}

package com.j3s.yobuddy.api.mentor;

import com.j3s.yobuddy.domain.mentor.menteeAssignment.dto.request.AssignMenteeRequest;
import com.j3s.yobuddy.domain.mentor.menteeAssignment.dto.response.MenteeCandidateResponse;
import com.j3s.yobuddy.domain.mentor.menteeAssignment.dto.response.MenteeDetailResponse;
import com.j3s.yobuddy.domain.mentor.menteeAssignment.dto.response.MenteeListResponse;
import com.j3s.yobuddy.domain.mentor.menteeAssignment.service.MentorService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/v1/mentors")
public class MentorController {

    private final MentorService mentorService;

    @PostMapping("/{mentorId}/mentees")
    public ResponseEntity<Void> assignMentee(
        @PathVariable Long mentorId,
        @RequestBody AssignMenteeRequest request
    ) {

        mentorService.assignMentee(mentorId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .build();
    }

//    @GetMapping("/{mentorId}/mentees")
//    public ResponseEntity<List<MenteeListResponse>> getMentees(@PathVariable Long mentorId) {
//
//        List<MenteeListResponse> mentees = mentorService.getMentees(mentorId);
//        return ResponseEntity.ok(mentees);
//    }

    @DeleteMapping("/{mentorId}/mentees/{menteeId}")
    public ResponseEntity<Void> removeMentee(
        @PathVariable Long mentorId,
        @PathVariable Long menteeId
    ) {

        mentorService.removeMentee(mentorId, menteeId);
        return ResponseEntity.noContent()
                             .build();
    }

    @GetMapping("/{mentorId}/department-newbies")
    public List<MenteeCandidateResponse> getNewbies(@PathVariable Long mentorId) {
        return mentorService.getDepartmentNewbies(mentorId);
    }

    @GetMapping("/{mentorId}/mentees/{menteeId}")
    public ResponseEntity<MenteeDetailResponse> getMenteeDetail(
        @PathVariable Long mentorId,
        @PathVariable Long menteeId) {

        return ResponseEntity.ok(mentorService.getMenteeDetail(mentorId, menteeId));
    }
}

package com.j3s.yobuddy.api.admin;

import com.j3s.yobuddy.domain.announcement.dto.request.AnnouncementCreateRequest;
import com.j3s.yobuddy.domain.announcement.dto.request.AnnouncementUpdateRequest;
import com.j3s.yobuddy.domain.announcement.dto.response.AnnouncementResponse;
import com.j3s.yobuddy.domain.announcement.entity.Announcement;
import com.j3s.yobuddy.domain.announcement.service.AnnouncementService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin/announcements")
public class AdminAnnouncementController {

    private final AnnouncementService announcementService;

    @PostMapping
    public ResponseEntity<AnnouncementResponse> createAnnouncement(
        @AuthenticationPrincipal String principal, @Valid @RequestBody
        AnnouncementCreateRequest request) {

        Long userId = Long.valueOf(principal);

        Announcement announcement = announcementService.createAnnouncement(userId, request);
        AnnouncementResponse response = AnnouncementResponse.from(announcement);

        return ResponseEntity
            .created(URI.create("/api/v1/admin/announcements/" + announcement.getAnnouncementId()))
            .body(response);
    }

    @PatchMapping("/{announcementId}")
    public ResponseEntity<AnnouncementResponse> updateAnnouncement(
        @PathVariable Long announcementId,
        @Valid @RequestBody
        AnnouncementUpdateRequest request) {
        AnnouncementResponse announcement = announcementService.updateAnnouncement(announcementId,
            request);

        return ResponseEntity.ok(announcement);
    }

    @DeleteMapping("/{announcementId}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long announcementId) {

        announcementService.deleteAnnouncement(announcementId);

        return ResponseEntity.noContent().build();
    }

}

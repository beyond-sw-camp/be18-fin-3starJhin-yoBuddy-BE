package com.j3s.yobuddy.api.admin;

import com.j3s.yobuddy.domain.announcement.dto.request.AnnouncementCreateRequest;
import com.j3s.yobuddy.domain.announcement.dto.request.AnnouncementUpdateRequest;
import com.j3s.yobuddy.domain.announcement.dto.response.AnnouncementResponse;
import com.j3s.yobuddy.domain.announcement.entity.Announcement;
import com.j3s.yobuddy.domain.announcement.entity.AnnouncementType;
import com.j3s.yobuddy.domain.announcement.service.AnnouncementService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin/announcements")
public class AdminAnnouncementController {

    private final AnnouncementService announcementService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AnnouncementResponse> createAnnouncement(
        @AuthenticationPrincipal String principal,
        @RequestParam String title,
        @RequestParam AnnouncementType type,
        @RequestParam(required = false) String content,
        @RequestPart(required = false) List<MultipartFile> files
    ) throws Exception {

        Long userId = Long.valueOf(principal);

        AnnouncementResponse response = announcementService.createAnnouncementWithFiles(
            userId, title, type, content, files
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping(value = "/{announcementId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AnnouncementResponse> updateAnnouncement(
        @PathVariable Long announcementId,
        @RequestParam(required = false) String title,
        @RequestParam(required = false) AnnouncementType type,
        @RequestParam(required = false) String content,
        @RequestParam(required = false) List<Long> removeFileIds,
        @RequestPart(required = false) List<MultipartFile> files
    ) throws Exception {

        AnnouncementResponse response = announcementService.updateAnnouncementWithFiles(
            announcementId, title, type, content, removeFileIds, files
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{announcementId}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long announcementId) {

        announcementService.deleteAnnouncement(announcementId);

        return ResponseEntity.noContent().build();
    }

}

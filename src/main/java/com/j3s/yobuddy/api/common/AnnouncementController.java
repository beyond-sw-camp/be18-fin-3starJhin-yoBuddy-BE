package com.j3s.yobuddy.api.common;

import com.j3s.yobuddy.domain.announcement.dto.response.AnnouncementListResponse;
import com.j3s.yobuddy.domain.announcement.dto.response.AnnouncementResponse;
import com.j3s.yobuddy.domain.announcement.service.AnnouncementService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    public ResponseEntity<List<AnnouncementListResponse>> getAllAnnouncements(
        @RequestParam(required = false) String title) {
        List<AnnouncementListResponse> announcements = announcementService.getAllAnnouncements(
            title);

        return ResponseEntity.ok(announcements);
    }

    @GetMapping("/{announcementId}")
    public ResponseEntity<AnnouncementResponse> getAnnouncementById(
        @PathVariable Long announcementId) {

        AnnouncementResponse announcement = announcementService.getAnnouncementById(announcementId);

        return ResponseEntity.ok(announcement);
    }

}

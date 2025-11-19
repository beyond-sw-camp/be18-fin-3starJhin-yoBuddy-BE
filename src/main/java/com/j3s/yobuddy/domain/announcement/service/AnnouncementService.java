package com.j3s.yobuddy.domain.announcement.service;

import com.j3s.yobuddy.domain.announcement.dto.request.AnnouncementCreateRequest;
import com.j3s.yobuddy.domain.announcement.dto.request.AnnouncementUpdateRequest;
import com.j3s.yobuddy.domain.announcement.dto.response.AnnouncementListResponse;
import com.j3s.yobuddy.domain.announcement.dto.response.AnnouncementResponse;
import com.j3s.yobuddy.domain.announcement.entity.Announcement;
import com.j3s.yobuddy.domain.announcement.entity.AnnouncementType;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AnnouncementService {

    Announcement createAnnouncement(Long userId, AnnouncementCreateRequest request);

    AnnouncementResponse updateAnnouncement(Long announcementId,
        @Valid AnnouncementUpdateRequest request);

    void deleteAnnouncement(Long announcementId);

    Page<AnnouncementListResponse> getAllAnnouncements(AnnouncementType type, String title,
        Pageable pageable);

    AnnouncementResponse getAnnouncementById(Long announcementId);
}

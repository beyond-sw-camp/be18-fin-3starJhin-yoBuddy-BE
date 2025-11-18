package com.j3s.yobuddy.domain.announcement.dto.response;

import com.j3s.yobuddy.domain.announcement.entity.Announcement;
import com.j3s.yobuddy.domain.announcement.entity.AnnouncementType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AnnouncementListResponse {

    private final Long announcementId;
    private final String title;
    private final AnnouncementType type;
    private final LocalDateTime createdAt;

    public static AnnouncementListResponse from(Announcement ann) {
        return AnnouncementListResponse.builder()
            .announcementId(ann.getAnnouncementId())
            .title(ann.getTitle())
            .type(ann.getType())
            .createdAt(ann.getCreatedAt())
            .build();
    }

}

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
public class AnnouncementResponse {

    private final Long announcementId;
    private final String title;
    private final AnnouncementType type;
    private final String content;
    private final String author;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static AnnouncementResponse from(Announcement ann) {
        return AnnouncementResponse.builder()
            .announcementId(ann.getAnnouncementId())
            .title(ann.getTitle())
            .type(ann.getType())
            .content(ann.getContent())
            .author(ann.getUser().getName())
            .createdAt(ann.getCreatedAt())
            .updatedAt(ann.getUpdatedAt())
            .build();
    }

}

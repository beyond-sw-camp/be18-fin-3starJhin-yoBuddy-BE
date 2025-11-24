package com.j3s.yobuddy.domain.announcement.dto.request;

import com.j3s.yobuddy.domain.announcement.entity.AnnouncementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AnnouncementCreateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    private final String title;
    
    @NotNull(message = "공지 유형은 필수입니다.")
    private final AnnouncementType type;

    private final String content;
}

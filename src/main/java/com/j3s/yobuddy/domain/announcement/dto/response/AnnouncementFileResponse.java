package com.j3s.yobuddy.domain.announcement.dto.response;

import com.j3s.yobuddy.domain.file.entity.FileEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AnnouncementFileResponse {

    private final Long fileId;
    private final String filename;
    private final String filepath;

    public static AnnouncementFileResponse from(FileEntity file) {
        return AnnouncementFileResponse.builder()
            .fileId(file.getFileId())
            .filename(file.getFilename())
            .filepath(file.getFilepath())
            .build();
    }
}

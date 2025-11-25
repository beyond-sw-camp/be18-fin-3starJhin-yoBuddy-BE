package com.j3s.yobuddy.common.dto;

import com.j3s.yobuddy.domain.file.entity.FileEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FileResponse {

    private final Long fileId;
    private final String filename;
    private final String filepath;

    public static FileResponse from(FileEntity file) {
        return FileResponse.builder()
            .fileId(file.getFileId())
            .filename(file.getFilename())
            .filepath(file.getFilepath())
            .build();
    }
}

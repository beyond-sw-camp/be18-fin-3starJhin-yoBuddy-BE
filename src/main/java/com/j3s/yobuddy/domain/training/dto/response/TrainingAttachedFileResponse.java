package com.j3s.yobuddy.domain.training.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrainingAttachedFileResponse {

    private final Long fileId;
    private final String filename;
    private final String filepath;

    public static TrainingAttachedFileResponse of(Long fileId, String filename, String filepath) {
        return TrainingAttachedFileResponse.builder()
            .fileId(fileId)
            .filename(filename)
            .filepath(filepath)
            .build();
    }
}
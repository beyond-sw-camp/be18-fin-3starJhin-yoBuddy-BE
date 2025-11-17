package com.j3s.yobuddy.domain.training.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrainingAttachedFileResponse {

    private Long fileId;
    private String filename;
    private String filepath;

    public static TrainingAttachedFileResponse of(Long fileId, String filename, String filepath) {
        return TrainingAttachedFileResponse.builder()
            .fileId(fileId)
            .filename(filename)
            .filepath(filepath)
            .build();
    }
}
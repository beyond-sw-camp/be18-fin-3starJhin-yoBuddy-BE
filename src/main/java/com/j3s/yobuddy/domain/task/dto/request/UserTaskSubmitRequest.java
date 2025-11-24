package com.j3s.yobuddy.domain.task.dto.request;

import lombok.Getter;

@Getter
public class UserTaskSubmitRequest {
    private String attachmentUrl;  // 파일 링크(Optional)
}

package com.j3s.yobuddy.domain.task.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
public class TaskSubmitRequest {

    private String comment;                 // optional
    private MultipartFile[] files;          // optional (nullable)

}

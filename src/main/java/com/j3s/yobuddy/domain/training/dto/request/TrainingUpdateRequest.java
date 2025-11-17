package com.j3s.yobuddy.domain.training.dto.request;

import com.j3s.yobuddy.domain.training.entity.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingUpdateRequest {

    private String title;
    private TrainingType type;    // ONLINE / OFFLINE
    private String description;

    // TODO: fileIds는 추후 Files 연동 시 사용
    // private List<Long> fileIds;
}

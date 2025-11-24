package com.j3s.yobuddy.domain.wiki.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WikiRequest {

    private final Long userId;
    private final String title;
    private final String content;
    private final Long parentId;
    private final Integer depth;

}

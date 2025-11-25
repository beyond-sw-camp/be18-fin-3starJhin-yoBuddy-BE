package com.j3s.yobuddy.domain.wiki.dto.response;

import java.time.LocalDateTime;

import com.j3s.yobuddy.domain.wiki.entity.Wiki;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WikiListResponse {

    private Long wikiId;
    private Long userId;
    private String title;
    private Long parentId;
    private Integer depth;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public static WikiListResponse from(Wiki w) {
        return WikiListResponse.builder()
            .wikiId(w.getWikiId())
            .userId(w.getUserId())
            .title(w.getTitle())
            .parentId(w.getParentId())
            .depth(w.getDepth())
            .createAt(w.getCreateAt())
            .updateAt(w.getUpdateAt())
            .build();
    }
}

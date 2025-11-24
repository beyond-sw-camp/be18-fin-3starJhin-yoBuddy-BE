package com.j3s.yobuddy.domain.wiki.dto.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.j3s.yobuddy.domain.wiki.entity.Wiki;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WikiTreeResponse {

    private Long wikiId;
    private Long userId;
    private String title;
    private String content;
    private Long parentId;
    private Integer depth;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    @Builder.Default
    private List<WikiTreeResponse> children = new ArrayList<>();

    public static WikiTreeResponse from(Wiki w) {
        return WikiTreeResponse.builder()
            .wikiId(w.getWikiId())
            .userId(w.getUserId())
            .title(w.getTitle())
            .content(w.getContent())
            .parentId(w.getParentId())
            .depth(w.getDepth())
            .createAt(w.getCreateAt())
            .updateAt(w.getUpdateAt())
            .build();
    }
}

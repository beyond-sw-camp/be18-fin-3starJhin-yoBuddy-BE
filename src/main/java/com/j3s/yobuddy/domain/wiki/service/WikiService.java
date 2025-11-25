package com.j3s.yobuddy.domain.wiki.service;

import java.util.List;

import com.j3s.yobuddy.domain.wiki.dto.request.WikiRequest;
import com.j3s.yobuddy.domain.wiki.dto.response.WikiListResponse;
import com.j3s.yobuddy.domain.wiki.dto.response.WikiResponse;

public interface WikiService {

    List<WikiListResponse> getWikis(String title);

    java.util.List<com.j3s.yobuddy.domain.wiki.dto.response.WikiTreeResponse> getWikiTree(String title);

    void createWiki(WikiRequest request);

    WikiListResponse updateWiki(Long wikiId, WikiRequest request);

    java.util.List<Long> deleteWiki(Long wikiId);

    WikiResponse getWikiById(Long wikiId);
}

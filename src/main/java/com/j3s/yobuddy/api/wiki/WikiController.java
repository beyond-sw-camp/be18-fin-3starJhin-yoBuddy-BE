package com.j3s.yobuddy.api.wiki;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.j3s.yobuddy.domain.wiki.dto.request.WikiRequest;
import com.j3s.yobuddy.domain.wiki.dto.response.WikiResponse;
import com.j3s.yobuddy.domain.wiki.service.WikiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wiki")
public class WikiController {

    private final WikiService wikiService;

    @GetMapping
    public ResponseEntity<java.util.List<com.j3s.yobuddy.domain.wiki.dto.response.WikiTreeResponse>> getWikis(@RequestParam(required = false) String title) {
        java.util.List<com.j3s.yobuddy.domain.wiki.dto.response.WikiTreeResponse> tree = wikiService.getWikiTree(title);
        return ResponseEntity.ok(tree);
    }

    @GetMapping("/{wikiId}")
    public ResponseEntity<WikiResponse> getWikiById(@PathVariable("wikiId") Long wikiId) {
        WikiResponse resp = wikiService.getWikiById(wikiId);
        return ResponseEntity.ok(resp);
    }

    @PostMapping
    public ResponseEntity<String> createWiki(@RequestBody WikiRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            return ResponseEntity.badRequest().body("title must not be empty");
        }

        wikiService.createWiki(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Wiki created");
    }

    @PatchMapping("/{wikiId}")
    public ResponseEntity<String> updateWiki(@PathVariable("wikiId") Long wikiId,
        @RequestBody WikiRequest request) {

        wikiService.updateWiki(wikiId, request);
        return ResponseEntity.ok("Wiki updated");
    }

    @DeleteMapping("/{wikiId}")
    public ResponseEntity<java.util.Map<String,Object>> deleteWiki(@PathVariable("wikiId") Long wikiId) {
        java.util.List<Long> deletedIds = wikiService.deleteWiki(wikiId);
        java.util.Map<String,Object> resp = new java.util.HashMap<>();
        resp.put("deletedCount", deletedIds.size());
        resp.put("deletedIds", deletedIds);
        return ResponseEntity.ok(resp);
    }
}

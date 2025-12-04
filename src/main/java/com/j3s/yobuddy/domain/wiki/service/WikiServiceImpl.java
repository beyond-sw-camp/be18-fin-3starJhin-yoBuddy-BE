package com.j3s.yobuddy.domain.wiki.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.j3s.yobuddy.domain.wiki.dto.request.WikiRequest;
import com.j3s.yobuddy.domain.wiki.dto.response.WikiListResponse;
import com.j3s.yobuddy.domain.wiki.dto.response.WikiResponse;
import com.j3s.yobuddy.domain.wiki.entity.Wiki;
import com.j3s.yobuddy.domain.wiki.repository.WikiRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WikiServiceImpl implements WikiService {

    private final WikiRepository wikiRepository;

    @Override
    @Transactional(readOnly = true)
    public List<WikiListResponse> getWikis(String title) {
        List<Wiki> result = (title == null || title.isBlank())
            ? wikiRepository.findAllByIsDeletedFalse()
            : wikiRepository.findByTitleContainingIgnoreCaseAndIsDeletedFalse(title);

        return result.stream().map(WikiListResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<com.j3s.yobuddy.domain.wiki.dto.response.WikiTreeResponse> getWikiTree(String title) {
        List<Wiki> flat = (title == null || title.isBlank())
            ? wikiRepository.findAllByIsDeletedFalse()
            : wikiRepository.findByTitleContainingIgnoreCaseAndIsDeletedFalse(title);

        // build id -> node map
        java.util.Map<Long, com.j3s.yobuddy.domain.wiki.dto.response.WikiTreeResponse> map = new java.util.HashMap<>();
        for (Wiki w : flat) {
            map.put(w.getWikiId(), com.j3s.yobuddy.domain.wiki.dto.response.WikiTreeResponse.from(w));
        }

        // attach children to parents
        java.util.List<com.j3s.yobuddy.domain.wiki.dto.response.WikiTreeResponse> roots = new java.util.ArrayList<>();
        for (com.j3s.yobuddy.domain.wiki.dto.response.WikiTreeResponse node : map.values()) {
            Long parentId = node.getParentId();
            if (parentId != null && map.containsKey(parentId)) {
                map.get(parentId).getChildren().add(node);
            } else {
                roots.add(node);
            }
        }

        // Optionally sort roots/children by depth or createAt if needed. For now return roots as-is.
        return roots;
    }

    @Override
    @Transactional
    public void createWiki(WikiRequest request) {
        Wiki w = Wiki.builder()
            .userId(request.getUserId())
            .title(request.getTitle())
            .content(request.getContent())
            .parentId(request.getParentId())
            .depth(request.getDepth())
            .createAt(LocalDateTime.now())
            .updateAt(LocalDateTime.now())
            .isDeleted(false)
            .build();

        wikiRepository.save(w);
    }

    @Override
    @Transactional
    public WikiListResponse updateWiki(Long wikiId, WikiRequest request) {
        Wiki w = wikiRepository.findByWikiIdAndIsDeletedFalse(wikiId)
            .orElseThrow(() -> new RuntimeException("Wiki not found: " + wikiId));

        w.update(request.getUserId(), request.getTitle(), request.getContent(), request.getParentId(), request.getDepth());

        wikiRepository.save(w);

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

    @Override
    @Transactional
    public java.util.List<Long> deleteWiki(Long wikiId) {
        Wiki root = wikiRepository.findByWikiIdAndIsDeletedFalse(wikiId)
            .orElseThrow(() -> new RuntimeException("Wiki not found: " + wikiId));

        java.util.List<Wiki> toDelete = new java.util.ArrayList<>();
        java.util.Deque<Wiki> stack = new java.util.ArrayDeque<>();
        java.util.Set<Long> visited = new java.util.HashSet<>();

        stack.push(root);

        while (!stack.isEmpty()) {
            Wiki cur = stack.pop();
            if (cur.getWikiId() == null) continue;
            if (visited.contains(cur.getWikiId())) continue;
            visited.add(cur.getWikiId());
            toDelete.add(cur);

            // fetch direct children from DB (non-deleted)
            java.util.List<Wiki> children = wikiRepository.findByParentIdAndIsDeletedFalse(cur.getWikiId());
            if (children != null && !children.isEmpty()) {
                for (Wiki c : children) {
                    if (c != null && c.getWikiId() != null && !visited.contains(c.getWikiId())) {
                        stack.push(c);
                    }
                }
            }
        }

        // soft delete all collected nodes and collect ids
        java.util.List<Long> deletedIds = new java.util.ArrayList<>();
        for (Wiki node : toDelete) {
            node.softDelete();
            if (node.getWikiId() != null) deletedIds.add(node.getWikiId());
        }

        wikiRepository.saveAll(toDelete);

        return deletedIds;
    }

    @Override
    @Transactional(readOnly = true)
    public WikiResponse getWikiById(Long wikiId) {
        Wiki w = wikiRepository.findById(wikiId)
            .orElseThrow(() -> new RuntimeException("Wiki not found: " + wikiId));

        return WikiResponse.from(w);
    }
}

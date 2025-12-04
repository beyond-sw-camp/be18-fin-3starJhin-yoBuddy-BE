package com.j3s.yobuddy.domain.wiki.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.j3s.yobuddy.domain.wiki.entity.Wiki;

public interface WikiRepository extends JpaRepository<Wiki, Long> {

    List<Wiki> findAllByIsDeletedFalse();

    List<Wiki> findByTitleContainingIgnoreCaseAndIsDeletedFalse(String title);

    java.util.Optional<Wiki> findByWikiIdAndIsDeletedFalse(Long wikiId);

    List<Wiki> findByParentIdAndIsDeletedFalse(Long parentId);
}

package com.j3s.yobuddy.domain.wiki.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wiki")
public class Wiki {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wiki_id", nullable = false)
    private Long wikiId;

    @Column(name = "user_id")
    private Long userId;

    @Column(length = 50)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "parent_id")
    private Long parentId;

    private Integer depth;

    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "isdeleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @PrePersist
    protected void onCreate() {
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }

    public void update(Long userId, String title, String content, Long parentId, Integer depth) {
        boolean updated = false;
        if (userId != null) {
            this.userId = userId;
            updated = true;
        }
        if (title != null) {
            this.title = title;
            updated = true;
        }
        if (content != null) {
            this.content = content;
            updated = true;
        }
        if (parentId != null) {
            this.parentId = parentId;
            updated = true;
        }
        if (depth != null) {
            this.depth = depth;
            updated = true;
        }
        if (updated) {
            this.updateAt = LocalDateTime.now();
        }
    }

    public void softDelete() {
        this.isDeleted = true;
        this.updateAt = LocalDateTime.now();
    }
}

package com.j3s.yobuddy.domain.announcement.repository;

import com.j3s.yobuddy.domain.announcement.entity.Announcement;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    Optional<Announcement> findByAnnouncementIdAndIsDeletedFalse(Long announcementId);

    Page<Announcement> findAllByIsDeletedFalse(Pageable pageable);

    Page<Announcement> findByTitleContainingIgnoreCaseAndIsDeletedFalse(String title,
        Pageable pageable);

    Announcement findByAnnouncementId(Long announcementId);
}

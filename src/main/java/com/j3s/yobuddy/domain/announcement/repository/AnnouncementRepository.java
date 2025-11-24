package com.j3s.yobuddy.domain.announcement.repository;

import com.j3s.yobuddy.domain.announcement.entity.Announcement;
import com.j3s.yobuddy.domain.announcement.entity.AnnouncementType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    Optional<Announcement> findByAnnouncementIdAndIsDeletedFalse(Long announcementId);

    Page<Announcement> findAllByIsDeletedFalse(Pageable pageable);

    Page<Announcement> findByTitleContainingIgnoreCaseAndIsDeletedFalse(String title,
        Pageable pageable);

    Page<Announcement> findByTypeAndIsDeletedFalse(AnnouncementType type, Pageable pageable);

    Page<Announcement> findByTypeAndTitleContainingIgnoreCaseAndIsDeletedFalse(
        AnnouncementType type, String title, Pageable pageable);

    Announcement findByAnnouncementId(Long announcementId);
}

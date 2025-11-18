package com.j3s.yobuddy.domain.announcement.repository;

import com.j3s.yobuddy.domain.announcement.entity.Announcement;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    Optional<Announcement> findByAnnouncementIdAndIsDeletedFalse(Long announcementId);

    List<Announcement> findAllByIsDeletedFalse();

    List<Announcement> findByTitleContainingIgnoreCaseAndIsDeletedFalse(String title);

    Announcement findByAnnouncementId(Long announcementId);
}

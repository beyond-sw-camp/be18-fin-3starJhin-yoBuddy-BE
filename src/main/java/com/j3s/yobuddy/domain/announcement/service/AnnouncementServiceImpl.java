package com.j3s.yobuddy.domain.announcement.service;

import com.j3s.yobuddy.domain.announcement.dto.request.AnnouncementCreateRequest;
import com.j3s.yobuddy.domain.announcement.dto.request.AnnouncementUpdateRequest;
import com.j3s.yobuddy.domain.announcement.dto.response.AnnouncementListResponse;
import com.j3s.yobuddy.domain.announcement.dto.response.AnnouncementResponse;
import com.j3s.yobuddy.domain.announcement.entity.Announcement;
import com.j3s.yobuddy.domain.announcement.entity.AnnouncementType;
import com.j3s.yobuddy.domain.announcement.exception.AnnouncementAlreadyDeletedException;
import com.j3s.yobuddy.domain.announcement.exception.AnnouncementNotFoundException;
import com.j3s.yobuddy.domain.announcement.repository.AnnouncementRepository;
import com.j3s.yobuddy.domain.user.entity.User;
import com.j3s.yobuddy.domain.user.exception.UserNotFoundException;
import com.j3s.yobuddy.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Announcement createAnnouncement(Long userId, AnnouncementCreateRequest request) {

        User admin = userRepository.findByUserIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        Announcement announcement = Announcement.builder()
            .title(request.getTitle())
            .type(request.getType())
            .content(request.getContent())
            .user(admin)
            .build();

        return announcementRepository.save(announcement);
    }

    @Override
    @Transactional
    public AnnouncementResponse updateAnnouncement(Long announcementId,
        AnnouncementUpdateRequest request) {

        Announcement announcement = announcementRepository.findByAnnouncementIdAndIsDeletedFalse(
            announcementId).orElseThrow(() -> new AnnouncementNotFoundException(announcementId));

        announcement.update(request.getTitle(), request.getType(), request.getContent());

        announcementRepository.save(announcement);

        return AnnouncementResponse.builder()
            .announcementId(announcement.getAnnouncementId())
            .title(announcement.getTitle())
            .type(announcement.getType())
            .content(announcement.getContent())
            .author(announcement.getUser().getName())
            .createdAt(announcement.getCreatedAt())
            .updatedAt(announcement.getUpdatedAt())
            .build();
    }

    @Override
    @Transactional
    public void deleteAnnouncement(Long announcementId) {
        Announcement announcement = announcementRepository.findByAnnouncementIdAndIsDeletedFalse(
            announcementId).orElseThrow(() -> new AnnouncementNotFoundException(announcementId));

        if (announcement.isDeleted()) {
            throw new AnnouncementAlreadyDeletedException(announcementId);
        }
        announcement.softDelete();
        announcementRepository.save(announcement);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnnouncementListResponse> getAllAnnouncements(AnnouncementType type, String title,
        Pageable pageable) {
        Page<Announcement> result;

        if (type == null && (title == null || title.isBlank())) {
            // 전체 조회
            result = announcementRepository.findAllByIsDeletedFalse(pageable);

        } else if (type == null) {
            // 타입 없이 제목 검색
            result = announcementRepository.findByTitleContainingIgnoreCaseAndIsDeletedFalse(title,
                pageable);

        } else if (title == null || title.isBlank()) {
            // 타입만 필터
            result = announcementRepository.findByTypeAndIsDeletedFalse(type, pageable);

        } else {
            // 타입 + 제목 검색
            result = announcementRepository.findByTypeAndTitleContainingIgnoreCaseAndIsDeletedFalse(
                type, title, pageable);
        }

        return result.map(AnnouncementListResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public AnnouncementResponse getAnnouncementById(Long announcementId) {

        Announcement announcement = announcementRepository.findByAnnouncementId(announcementId);

        if (announcement.isDeleted()) {
            throw new AnnouncementAlreadyDeletedException(announcementId);
        }

        return AnnouncementResponse.from(announcement);
    }
}

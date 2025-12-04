package com.j3s.yobuddy.domain.announcement.service;

import com.j3s.yobuddy.domain.announcement.dto.response.AnnouncementListResponse;
import com.j3s.yobuddy.domain.announcement.dto.response.AnnouncementResponse;
import com.j3s.yobuddy.domain.announcement.entity.Announcement;
import com.j3s.yobuddy.domain.announcement.entity.AnnouncementType;
import com.j3s.yobuddy.domain.announcement.exception.AnnouncementAlreadyDeletedException;
import com.j3s.yobuddy.domain.announcement.exception.AnnouncementNotFoundException;
import com.j3s.yobuddy.domain.announcement.repository.AnnouncementRepository;
import com.j3s.yobuddy.domain.file.entity.FileEntity;
import com.j3s.yobuddy.domain.file.entity.FileType;
import com.j3s.yobuddy.domain.file.entity.RefType;
import com.j3s.yobuddy.domain.file.repository.FileRepository;
import com.j3s.yobuddy.domain.file.service.FileService;
import com.j3s.yobuddy.domain.notification.entity.NotificationType;
import com.j3s.yobuddy.domain.notification.service.NotificationService;
import com.j3s.yobuddy.domain.user.entity.Role;
import com.j3s.yobuddy.domain.user.entity.User;
import com.j3s.yobuddy.domain.user.exception.UserNotFoundException;
import com.j3s.yobuddy.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final FileRepository fileRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public AnnouncementResponse createAnnouncementWithFiles(
        Long userId,
        String title,
        AnnouncementType type,
        String content,
        List<MultipartFile> files
    ) throws Exception {

        User admin = userRepository.findByUserIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        Announcement announcement = announcementRepository.save(
            Announcement.builder()
                .title(title)
                .type(type)
                .content(content)
                .user(admin)
                .build()
        );

        if (files != null) {
            for (MultipartFile file : files) {
                FileEntity uploaded = fileService.uploadTempFile(file, FileType.GENERAL);
                fileService.bindFile(uploaded.getFileId(), RefType.ANNOUNCEMENT, announcement.getAnnouncementId());
            }
        }

        List<FileEntity> fileList =
            fileRepository.findByRefTypeAndRefId(RefType.ANNOUNCEMENT, announcement.getAnnouncementId());

        notifyUsersForNewAnnouncement();

        return AnnouncementResponse.from(announcement, fileList);
    }

    @Override
    @Transactional
    public AnnouncementResponse updateAnnouncementWithFiles(
        Long announcementId,
        String title,
        AnnouncementType type,
        String content,
        List<Long> removeFileIds,
        List<MultipartFile> files
    ) throws Exception {

        Announcement ann = announcementRepository
            .findByAnnouncementIdAndIsDeletedFalse(announcementId)
            .orElseThrow(() -> new AnnouncementNotFoundException(announcementId));

        ann.update(title, type, content);

        if (removeFileIds != null) {
            for (Long fileId : removeFileIds) {
                FileEntity file = fileService.getFileEntity(fileId);
                file.setRefId(null);
                file.setRefType(null);
            }
        }

        if (files != null) {
            for (MultipartFile file : files) {
                FileEntity uploaded = fileService.uploadTempFile(file, FileType.GENERAL);
                fileService.bindFile(uploaded.getFileId(), RefType.ANNOUNCEMENT, announcementId);
            }
        }

        List<FileEntity> fileList =
            fileRepository.findByRefTypeAndRefId(RefType.ANNOUNCEMENT, announcementId);

        return AnnouncementResponse.from(ann, fileList);
    }

    @Override
    @Transactional
    public void deleteAnnouncement(Long announcementId) {

        Announcement announcement = announcementRepository
            .findByAnnouncementIdAndIsDeletedFalse(announcementId)
            .orElseThrow(() -> new AnnouncementNotFoundException(announcementId));

        if (announcement.isDeleted()) {
            throw new AnnouncementAlreadyDeletedException(announcementId);
        }

        announcement.softDelete();

        List<FileEntity> files =
            fileRepository.findByRefTypeAndRefId(RefType.ANNOUNCEMENT, announcementId);

        for (FileEntity file : files) {
            file.setRefId(null);
            file.setRefType(null);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnnouncementListResponse> getAllAnnouncements(AnnouncementType type, String title,
        Pageable pageable) {

        Page<Announcement> result;

        if (type == null && (title == null || title.isBlank())) {
            result = announcementRepository.findAllByIsDeletedFalse(pageable);

        } else if (type == null) {
            result = announcementRepository.findByTitleContainingIgnoreCaseAndIsDeletedFalse(title, pageable);

        } else if (title == null || title.isBlank()) {
            result = announcementRepository.findByTypeAndIsDeletedFalse(type, pageable);

        } else {
            result = announcementRepository.findByTypeAndTitleContainingIgnoreCaseAndIsDeletedFalse(
                type, title, pageable);
        }

        return result.map(AnnouncementListResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public AnnouncementResponse getAnnouncementById(Long announcementId) {

        Announcement ann = announcementRepository
            .findByAnnouncementIdAndIsDeletedFalse(announcementId)
            .orElseThrow(() -> new AnnouncementNotFoundException(announcementId));

        List<FileEntity> files =
            fileRepository.findByRefTypeAndRefId(RefType.ANNOUNCEMENT, announcementId);

        return AnnouncementResponse.from(ann, files);
    }

    private void notifyUsersForNewAnnouncement() {
        userRepository.findAllByIsDeletedFalse().stream()
            .filter(user -> user.getRole() == Role.USER || user.getRole() == Role.MENTOR)
            .forEach(user -> notificationService.notify(
                user,
                NotificationType.NEW_ANNOUNCEMENT,
                "새로운 공지사항",
                "새로운 공지사항이 등록되었어요."
            ));
    }
}

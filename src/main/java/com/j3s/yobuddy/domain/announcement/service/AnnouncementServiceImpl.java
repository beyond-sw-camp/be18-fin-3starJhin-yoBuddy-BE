package com.j3s.yobuddy.domain.announcement.service;

import com.j3s.yobuddy.domain.announcement.dto.request.AnnouncementUpdateRequest;
import com.j3s.yobuddy.domain.announcement.dto.response.AnnouncementListResponse;
import com.j3s.yobuddy.domain.announcement.dto.response.AnnouncementResponse;
import com.j3s.yobuddy.domain.announcement.entity.Announcement;
import com.j3s.yobuddy.domain.announcement.entity.AnnouncementType;
import com.j3s.yobuddy.domain.announcement.exception.AnnouncementAlreadyDeletedException;
import com.j3s.yobuddy.domain.announcement.exception.AnnouncementNotFoundException;
import com.j3s.yobuddy.domain.announcement.repository.AnnouncementRepository;
import com.j3s.yobuddy.domain.file.entity.FileEntity;
import com.j3s.yobuddy.domain.file.repository.FileRepository;
import com.j3s.yobuddy.domain.file.service.FileService;
import com.j3s.yobuddy.domain.file.entity.FileType;
import com.j3s.yobuddy.domain.file.entity.RefType;
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

//        if (files != null) {
//            for (MultipartFile file : files) {
//                FileEntity saved = fileService.uploadFile(
//                    file,
//                    FileType.GENERAL,
//                    RefType.ANNOUNCEMENT,
//                    announcement.getAnnouncementId()
//                );
//            }
//        }

        List<FileEntity> fileList =
            fileRepository.findByRefTypeAndRefId(RefType.ANNOUNCEMENT, announcement.getAnnouncementId());

        return AnnouncementResponse.from(announcement, fileList);
    }

    @Override
    @Transactional
    public AnnouncementResponse updateAnnouncement(Long announcementId,
        AnnouncementUpdateRequest request) {

        Announcement announcement = announcementRepository
            .findByAnnouncementIdAndIsDeletedFalse(announcementId)
            .orElseThrow(() -> new AnnouncementNotFoundException(announcementId));

        announcement.update(request.getTitle(), request.getType(), request.getContent());

        if (request.getRemoveFileIds() != null) {
            for (Long fileId : request.getRemoveFileIds()) {
                FileEntity file = fileService.getFileEntity(fileId);
                file.setRefId(null);
                file.setRefType(null);
            }
        }

        if (request.getAddFileIds() != null) {
            for (Long fileId : request.getAddFileIds()) {
                FileEntity file = fileService.getFileEntity(fileId);
                file.setRefType(RefType.ANNOUNCEMENT);
                file.setRefId(announcementId);
            }
        }

        List<FileEntity> fileList =
            fileRepository.findByRefTypeAndRefId(RefType.ANNOUNCEMENT, announcementId);

        return AnnouncementResponse.from(announcement, fileList);
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

        Announcement ann = announcementRepository
            .findByAnnouncementIdAndIsDeletedFalse(announcementId)
            .orElseThrow(() -> new AnnouncementNotFoundException(announcementId));

        List<FileEntity> files =
            fileRepository.findByRefTypeAndRefId(RefType.ANNOUNCEMENT, announcementId);

        return AnnouncementResponse.from(ann, files);
    }
}

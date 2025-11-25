package com.j3s.yobuddy.domain.file.service;

import com.j3s.yobuddy.domain.file.entity.FileEntity;
import com.j3s.yobuddy.domain.file.entity.FileType;
import com.j3s.yobuddy.domain.file.entity.RefType;
import com.j3s.yobuddy.domain.file.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;

@Service
public class FileService {

    @Autowired
    private SftpRemoteFileTemplate sftpTemplate;

    @Autowired
    private FileRepository fileRepository;

    @Value("${spring.file.upload-dir}")
    private String remoteDir; // QA 서버 경로

    // ----------------------------
    // 파일 업로드
    // ----------------------------
    public FileEntity uploadFile(MultipartFile multipartFile, FileType fileType, RefType refType, Long refId) throws Exception {
        // 1. MultipartFile → 임시 File
        File tempFile = new File(System.getProperty("java.io.tmpdir"), multipartFile.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }

// 2. SFTP 저장 폴더 구조 생성 (FileType까지만 분기)
        String folderPath = remoteDir + "/" + fileType.name(); // 기본: C:/.../TASK

        String remotePath = folderPath + "/" + multipartFile.getOriginalFilename();

        // final로 복사하여 람다에서 참조 가능하게 함
        final String finalFolderPath = folderPath;
        final String finalRemotePath = remotePath;

        // 3. SFTP 업로드
        sftpTemplate.execute(session -> {
            // 폴더 순차 생성
            String[] dirs = finalFolderPath.split("/");
            String currentPath = "";
            for (String dir : dirs) {
                if (dir.isEmpty()) continue;
                currentPath += "/" + dir;
                try {
                    session.mkdir(currentPath);
                } catch (Exception ignored) {
                    // 이미 존재하면 무시
                }
            }

            // 파일 업로드
            try (var inputStream = multipartFile.getInputStream()) {
                session.write(inputStream, finalRemotePath);
            }
            return null;
        });

        // 4. 임시 파일 삭제
        tempFile.delete();

        // 5. DB 저장
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileType(fileType);
        fileEntity.setRefType(refType);
        fileEntity.setRefId(refId);
        fileEntity.setFilename(multipartFile.getOriginalFilename());
        fileEntity.setFilepath(finalRemotePath);
        fileEntity.setUploadedAt(LocalDateTime.now());

        return fileRepository.save(fileEntity);
    }

    // ----------------------------
    // 파일 다운로드
    // ----------------------------
    public byte[] downloadFile(Long fileId) throws Exception {
        FileEntity fileEntity = getFileEntity(fileId);

        return sftpTemplate.execute(session -> {
            try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
                session.read(fileEntity.getFilepath(), baos);
                return baos.toByteArray();
            }
        });
    }

    // ----------------------------
    // 파일 엔티티 조회
    // ----------------------------
    public FileEntity getFileEntity(Long fileId) {
        return fileRepository.findById(fileId)
            .orElseThrow(() -> new RuntimeException("파일 없음"));
    }
}

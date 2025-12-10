package com.j3s.yobuddy.domain.file.service;

import com.j3s.yobuddy.domain.file.entity.FileEntity;
import com.j3s.yobuddy.domain.file.entity.FileType;
import com.j3s.yobuddy.domain.file.entity.RefType;
import com.j3s.yobuddy.domain.file.repository.FileRepository;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileService {

    private final SftpRemoteFileTemplate sftpTemplate;

    private final FileRepository fileRepository;

    @Value("${spring.file.upload-dir}")
    private String remoteDir;

    /**
     * 1) 임시 업로드 (refType/refId 없음)
     */
    public FileEntity uploadTempFile(MultipartFile file, FileType fileType) throws Exception {
        File tmp = new File(System.getProperty("java.io.tmpdir"), file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(tmp)) {
            fos.write(file.getBytes());
        }

        String folder = remoteDir + "/" + fileType.name();
        String path = folder + "/" + file.getOriginalFilename();

        final String finalFolder = folder;
        final String finalPath = path;

        sftpTemplate.execute(session -> {
            String[] dirs = finalFolder.split("/");
            String current = "";
            for (String d : dirs) {
                if (d.isEmpty()) continue;
                current += "/" + d;
                try { session.mkdir(current); } catch (Exception ignore) {}
            }

            try (var inputStream = file.getInputStream()) {
                session.write(inputStream, finalPath);
            }
            return null;
        });

        tmp.delete();

        FileEntity entity = new FileEntity();
        entity.setFileType(fileType);
        entity.setFilename(file.getOriginalFilename());
        entity.setFilepath(path);
        entity.setUploadedAt(LocalDateTime.now());

        return fileRepository.save(entity);
    }

    /**
     * 2) refType/refId 매핑 전용 (기존 uploadFile 역할)
     */
    public FileEntity bindFile(Long fileId, RefType refType, Long refId) {
        FileEntity entity = getFileEntity(fileId);
        entity.setRefType(refType);
        entity.setRefId(refId);
        return fileRepository.save(entity);
    }

    public FileEntity getFileEntity(Long fileId) {
        return fileRepository.findById(fileId)
            .orElseThrow(() -> new RuntimeException("파일 없음"));
    }

    public byte[] downloadFile(Long fileId) throws Exception {
        FileEntity file = getFileEntity(fileId);
        return sftpTemplate.execute(session -> {
            try (var baos = new java.io.ByteArrayOutputStream()) {
                session.read(file.getFilepath(), baos);
                return baos.toByteArray();
            }
        });
    }

    public void deleteFile(Long fileId) {
        FileEntity file = fileRepository.findById(fileId)
            .orElse(null);

        if (file == null) return;

        // 1) SFTP 파일 삭제
        try {
            sftpTemplate.execute(session -> {
                session.remove(file.getFilepath());
                return null;
            });
        } catch (Exception ignored) {
            // 파일이 없어도 무시
        }

        // 2) DB 삭제
        fileRepository.delete(file);
    }
}
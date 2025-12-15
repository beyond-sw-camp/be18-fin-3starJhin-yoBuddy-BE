package com.j3s.yobuddy.domain.file.service;

import com.j3s.yobuddy.domain.file.entity.FileEntity;
import com.j3s.yobuddy.domain.file.entity.FileType;
import com.j3s.yobuddy.domain.file.entity.RefType;
import com.j3s.yobuddy.domain.file.repository.FileRepository;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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

    public void downloadFileWithStreaming(Long fileId, OutputStream outputStream) throws Exception {
        FileEntity file = getFileEntity(fileId);  // 파일 엔티티 가져오기

        // SFTP에서 파일을 스트리밍 방식으로 다운로드
        sftpTemplate.execute(session -> {
            try {
                // 파일 경로와 출력 스트림을 두 번째 인수로 전달
                session.read(file.getFilepath(), outputStream);  // 파일 경로와 출력 스트림을 전달
                outputStream.flush();
            } catch (Exception e) {
                throw new RuntimeException("파일 다운로드 중 오류 발생", e);
            }
            return null;
        });
    }

    public void downloadFile(Long fileId, OutputStream outputStream) throws Exception {
        downloadFileWithStreaming(fileId, outputStream);  // 스트리밍 다운로드 호출
    }

    public void deleteFile(Long fileId) {
        FileEntity file = fileRepository.findById(fileId).orElse(null);

        if (file == null) return;

        // SFTP에서 파일 삭제
        try {
            sftpTemplate.execute(session -> {
                session.remove(file.getFilepath());
                return null;
            });
        } catch (Exception ignored) {
            // 파일이 없어도 무시
        }

        // DB에서 파일 삭제
        fileRepository.delete(file);
    }
}

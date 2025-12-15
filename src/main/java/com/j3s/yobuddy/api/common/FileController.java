package com.j3s.yobuddy.api.common;

import com.j3s.yobuddy.common.dto.FileResponse;
import com.j3s.yobuddy.domain.file.entity.FileEntity;
import com.j3s.yobuddy.domain.file.service.FileService;
import com.j3s.yobuddy.domain.file.entity.FileType;
import com.j3s.yobuddy.domain.file.entity.RefType;
import jakarta.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<FileResponse> uploadFile(
        @RequestParam("file") MultipartFile file,
        @RequestParam("fileType") FileType fileType
    ) throws Exception {

        FileEntity saved = fileService.uploadTempFile(file, fileType);
        return ResponseEntity.ok(FileResponse.from(saved));
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Void> downloadFile(@PathVariable Long fileId, HttpServletResponse response) throws Exception {
        // 파일 엔티티 가져오기
        FileEntity fileEntity = fileService.getFileEntity(fileId);

        // 파일 다운로드 스트리밍 방식으로 처리
        response.setContentType("application/octet-stream");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileEntity.getFilename() + "\"");

        // 스트리밍 방식으로 파일 다운로드
        try (OutputStream outputStream = response.getOutputStream()) {
            fileService.downloadFileWithStreaming(fileId, outputStream);  // 스트리밍 방식 다운로드
        } catch (Exception e) {
            throw new RuntimeException("파일 다운로드 중 오류 발생", e);
        }

        return ResponseEntity.ok().build();
    }
}

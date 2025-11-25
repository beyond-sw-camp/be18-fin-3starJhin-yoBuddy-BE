package com.j3s.yobuddy.api.common;

import com.j3s.yobuddy.common.dto.FileResponse;
import com.j3s.yobuddy.domain.file.entity.FileEntity;
import com.j3s.yobuddy.domain.file.service.FileService;
import com.j3s.yobuddy.domain.file.entity.FileType;
import com.j3s.yobuddy.domain.file.entity.RefType;
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
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) throws Exception {

        FileEntity fileEntity = fileService.getFileEntity(fileId);
        byte[] data = fileService.downloadFile(fileId);

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileEntity.getFilename() + "\"")
            .body(new ByteArrayResource(data));
    }
}

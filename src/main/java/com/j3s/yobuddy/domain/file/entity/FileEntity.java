// file: src/main/java/com/j3s/yobuddy/domain/file/FileEntity.java
package com.j3s.yobuddy.domain.file.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Files")
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    @Enumerated(EnumType.STRING)
    private FileType fileType; // TASK, TRAINING, FEEDBACK, FORM, GENERAL

    @Enumerated(EnumType.STRING)
    private RefType refType; // USER_TASK, PROGRAM_TASK 등

    private Long refId; // 연관 객체 ID

    private String filename;

    private String filepath; // SFTP 경로

    private LocalDateTime uploadedAt;

    // Getter / Setter
    public Long getFileId() { return fileId; }
    public void setFileId(Long fileId) { this.fileId = fileId; }

    public FileType getFileType() { return fileType; }
    public void setFileType(FileType fileType) { this.fileType = fileType; }

    public RefType getRefType() { return refType; }
    public void setRefType(RefType refType) { this.refType = refType; }

    public Long getRefId() { return refId; }
    public void setRefId(Long refId) { this.refId = refId; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getFilepath() { return filepath; }
    public void setFilepath(String filepath) { this.filepath = filepath; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}

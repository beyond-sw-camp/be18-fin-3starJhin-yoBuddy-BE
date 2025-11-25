// file: src/main/java/com/j3s/yobuddy/domain/file/FileRepository.java
package com.j3s.yobuddy.domain.file.repository;

import com.j3s.yobuddy.domain.file.entity.FileEntity;
import com.j3s.yobuddy.domain.file.entity.RefType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    List<FileEntity> findByRefTypeAndRefId(RefType refType, Long refId);
}

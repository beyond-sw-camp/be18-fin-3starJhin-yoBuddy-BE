package com.j3s.yobuddy.domain.announcement.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class AnnouncementNotFoundException extends BusinessException {

    private final HttpStatus status = HttpStatus.NOT_FOUND;

    public AnnouncementNotFoundException(Long id) {
        super("존재하지 않거나 삭제된 공지사항입니다. (id=" + id + ")", HttpStatus.NOT_FOUND);
    }
}

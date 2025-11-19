package com.j3s.yobuddy.domain.mentor.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AssignMenteeRequest {
    private final List<Long> menteeIds;
}

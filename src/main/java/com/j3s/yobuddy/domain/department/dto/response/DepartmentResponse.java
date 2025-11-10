package com.j3s.yobuddy.domain.department.dto.response;

import com.j3s.yobuddy.domain.department.entity.Department;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class DepartmentResponse {

    private final Long departmentId;
    private final String name;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private List<Map<String, Object>> users;

    public static DepartmentResponse from(Department entity) {
        return DepartmentResponse.builder()
            .departmentId(entity.getDepartmentId())
            .name(entity.getName())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .users(
                entity.getUsers().stream()
                    .map(user -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("userId", user.getUserId());
                        map.put("name", user.getName());
                        map.put("email", user.getEmail());
                        map.put("role", user.getRole()); // String이면 .name() 제거
                        return map;
                    })
                    .collect(Collectors.toList()) // ✅ Java 8 호환
            )
            .build();
    }
}

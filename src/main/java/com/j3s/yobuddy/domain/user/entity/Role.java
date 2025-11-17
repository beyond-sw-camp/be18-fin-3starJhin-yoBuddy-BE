package com.j3s.yobuddy.domain.user.entity;

public enum Role {
    USER,
    ADMIN,
    MENTOR;

    public boolean isMentor() {
        return this == MENTOR;
    }
}

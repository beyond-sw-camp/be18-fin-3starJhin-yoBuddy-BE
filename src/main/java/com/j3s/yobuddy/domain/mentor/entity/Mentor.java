package com.j3s.yobuddy.domain.mentor.entity;

import com.j3s.yobuddy.domain.user.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "mentors")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Mentor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mentor_id")
    private Long mentorId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<MentorMenteeAssignment> assignments = new ArrayList<>();

    @Column(length = 100, nullable = false)
    private String position;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static Mentor create(User user, String position) {
        return Mentor.builder()
                     .user(user)
                     .position(position)
                     .deleted(false)
                     .build();
    }

    public void addMentee(MentorMenteeAssignment assignment) {
        this.assignments.add(assignment);
    }


    public void updatePosition(String position) {
        if (position != null && !position.isBlank()) {
            this.position = position;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void softDelete() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }
}

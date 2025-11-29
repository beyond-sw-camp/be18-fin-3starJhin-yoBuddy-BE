package com.j3s.yobuddy.domain.user.dashboard.repository;

import com.j3s.yobuddy.common.dto.FileResponse;
import com.j3s.yobuddy.domain.file.entity.RefType;
import com.j3s.yobuddy.domain.file.repository.FileRepository;
import com.j3s.yobuddy.domain.mentor.menteeAssignment.entity.QMentorMenteeAssignment;
import com.j3s.yobuddy.domain.user.dashboard.response.MentorResponse;
import com.j3s.yobuddy.domain.user.entity.QUser;
import com.j3s.yobuddy.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {

    private final JPAQueryFactory query;
    private final FileRepository fileRepository;
    private final QMentorMenteeAssignment ma = QMentorMenteeAssignment.mentorMenteeAssignment;
    private final QUser user = QUser.user;

    @Override
    public MentorResponse getMentor(Long userId) {
        User mentor = query
            .select(user)
            .from(ma)
            .join(user).on(user.userId.eq(ma.mentor.userId))
            .where(ma.mentee.userId.eq(userId))
            .fetchOne();

        if (mentor == null) {
            return null;
        }

        String profileImageUrl = fileRepository
            .findByRefTypeAndRefId(RefType.USER_PROFILE, userId)
            .stream()
            .findFirst()
            .map(FileResponse::from)
            .map(FileResponse::getUrl)
            .orElse(null);

        return new MentorResponse(
            mentor.getUserId(),
            mentor.getName(),
            mentor.getEmail(),
            mentor.getPhoneNumber(),
            mentor.getDepartment().getName(),
            profileImageUrl
        );
    }
}

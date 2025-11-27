package com.j3s.yobuddy.domain.mentoring.repository;

import com.j3s.yobuddy.domain.mentoring.entity.MentoringSession;
import com.j3s.yobuddy.domain.mentoring.entity.MentoringStatus;
import com.j3s.yobuddy.domain.mentoring.entity.QMentoringSession;
import com.j3s.yobuddy.domain.user.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class MentoringSessionQueryRepositoryImpl implements MentoringSessionQueryRepository {

    private final JPAQueryFactory queryFactory;

    QMentoringSession ms = QMentoringSession.mentoringSession;
    QUser mentor = new QUser("mentor");
    QUser mentee = new QUser("mentee");

    @Override
    public Page<MentoringSession> searchSessions(
        Long mentorId,
        Long menteeId,
        Long programId,
        MentoringStatus status,
        String query,
        Pageable pageable
    ) {

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(ms.deleted.isFalse());

        if (mentorId != null) {
            builder.and(ms.mentor.userId.eq(mentorId));
        }

        if (menteeId != null) {
            builder.and(ms.mentee.userId.eq(menteeId));
        }

        if (programId != null) {
            builder.and(ms.program.programId.eq(programId));
        }

        if (status != null) {
            builder.and(ms.status.eq(status));
        }

        if (query != null && !query.isBlank()) {
            String likeQuery = "%" + query.toLowerCase() + "%";
            builder.and(
                ms.mentor.name.lower().like(likeQuery)
                    .or(ms.mentee.name.lower().like(likeQuery))
            );
        }

        List<MentoringSession> content = queryFactory
            .selectFrom(ms)
            .leftJoin(ms.mentor, mentor).fetchJoin()
            .leftJoin(ms.mentee, mentee).fetchJoin()
            .where(builder)
            .orderBy(ms.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long count = queryFactory
            .select(ms.count())
            .from(ms)
            .where(builder)
            .fetchOne();

        return new PageImpl<>(content, pageable, count);
    }
}

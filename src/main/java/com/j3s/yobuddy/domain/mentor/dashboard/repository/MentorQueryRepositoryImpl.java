package com.j3s.yobuddy.domain.mentor.dashboard.repository;

import com.j3s.yobuddy.common.dto.FileResponse;
import com.j3s.yobuddy.domain.department.entity.QDepartment;
import com.j3s.yobuddy.domain.file.entity.RefType;
import com.j3s.yobuddy.domain.file.repository.FileRepository;
import com.j3s.yobuddy.domain.mentor.dashboard.response.MenteeListResponse;
import com.j3s.yobuddy.domain.mentor.dashboard.response.MentorSummaryResponse;
import com.j3s.yobuddy.domain.mentor.menteeAssignment.entity.QMentorMenteeAssignment;
import com.j3s.yobuddy.domain.mentor.mentoring.entity.QMentoringSession;
import com.j3s.yobuddy.domain.training.entity.QUserTraining;
import com.j3s.yobuddy.domain.training.entity.UserTrainingStatus;
import com.j3s.yobuddy.domain.user.entity.QUser;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MentorQueryRepositoryImpl implements MentorQueryRepository {

    private final JPAQueryFactory query;
    private final FileRepository fileRepository;

    private final QMentoringSession ms = QMentoringSession.mentoringSession;
    private final QMentorMenteeAssignment ma = QMentorMenteeAssignment.mentorMenteeAssignment;
    private final QUser user = QUser.user;
    private final QUserTraining ut = QUserTraining.userTraining;
    private final QDepartment dept = QDepartment.department;

    @Override
    public MentorSummaryResponse getMentorSummary(Long mentorId) {

        LocalDate today = LocalDate.now();

        long todaySessions = query
            .select(ms.count())
            .from(ms)
            .where(
                ms.mentor.userId.eq(mentorId),
                ms.scheduledAt.year().eq(today.getYear()),
                ms.scheduledAt.month().eq(today.getMonthValue()),
                ms.scheduledAt.dayOfMonth().eq(today.getDayOfMonth())
            )
            .fetchOne();

        long totalSessions = query
            .select(ms.count())
            .from(ms)
            .where(ms.mentor.userId.eq(mentorId))
            .fetchOne();

        long pendingFeedback = query
            .select(ms.count())
            .from(ms)
            .where(
                ms.mentor.userId.eq(mentorId),
                ms.feedback.isNull()
            )
            .fetchOne();

        long pendingEvaluation = query
            .select(ut.count())
            .from(ut)
            .join(ut.user, user)
            .join(ma).on(ma.mentee.userId.eq(user.userId))
            .where(
                ma.mentor.userId.eq(mentorId),
                ma.deleted.isFalse(),
                ut.status.eq(UserTrainingStatus.PENDING)
            )
            .fetchOne();

        return new MentorSummaryResponse(
            todaySessions,
            totalSessions,
            pendingFeedback,
            pendingEvaluation
        );
    }

    @Override
    public MenteeListResponse getMentees(Long mentorId) {

        List<Tuple> rows = query
            .select(
                user.userId,
                user.name,
                user.email,
                user.phoneNumber,
                dept.name,
                ut.status
            )
            .from(ma)
            .join(ma.mentee, user)
            .leftJoin(user.department, dept)
            .leftJoin(ut).on(ut.user.userId.eq(user.userId))
            .where(
                ma.mentor.userId.eq(mentorId),
                ma.deleted.isFalse()
            )
            .fetch();

        Map<Long, MenteeListResponse.MenteeItem> result = rows.stream()
            .collect(Collectors.groupingBy(
                r -> r.get(user.userId),
                Collectors.collectingAndThen(Collectors.toList(), items -> {

                    Long menteeId = items.get(0).get(user.userId);
                    String name = items.get(0).get(user.name);
                    String email = items.get(0).get(user.email);
                    String phoneNumber = items.get(0).get(user.phoneNumber);

                    String profileImageUrl = fileRepository
                        .findByRefTypeAndRefId(RefType.USER_PROFILE, menteeId)
                        .stream()
                        .findFirst()
                        .map(FileResponse::from)
                        .map(FileResponse::getUrl)
                        .orElse(null);

                    String departmentName = items.get(0).get(dept.name);

                    int completed = (int) items.stream()
                        .filter(i -> i.get(ut.status) == UserTrainingStatus.COMPLETED)
                        .count();

                    int pending = (int) items.stream()
                        .filter(i -> i.get(ut.status) == UserTrainingStatus.PENDING)
                        .count();

                    return new MenteeListResponse.MenteeItem(
                        menteeId,
                        name,
                        email,
                        phoneNumber,
                        departmentName,
                        profileImageUrl,
                        completed,
                        pending
                    );
                })
            ));

        return new MenteeListResponse(result.values().stream().toList());
    }
}
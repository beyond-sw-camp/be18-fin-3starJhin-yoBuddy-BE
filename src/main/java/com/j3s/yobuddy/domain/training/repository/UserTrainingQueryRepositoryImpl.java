package com.j3s.yobuddy.domain.training.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.j3s.yobuddy.domain.formresult.entity.QFormResult;
import com.j3s.yobuddy.domain.training.dto.response.UserTrainingDetailResponse;
import com.j3s.yobuddy.domain.training.dto.response.UserTrainingItemResponse;
import com.j3s.yobuddy.domain.training.entity.QProgramTraining;
import com.j3s.yobuddy.domain.training.entity.QTraining;
import com.j3s.yobuddy.domain.training.entity.QUserTraining;
import com.j3s.yobuddy.domain.training.entity.TrainingType;
import com.j3s.yobuddy.domain.training.entity.UserTraining;
import com.j3s.yobuddy.domain.training.entity.UserTrainingStatus;
import com.j3s.yobuddy.domain.user.entity.QUser;
import com.j3s.yobuddy.domain.user.entity.Role;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserTrainingQueryRepositoryImpl implements UserTrainingQueryRepository{

    private final JPAQueryFactory query;
    private final QUserTraining ut = QUserTraining.userTraining;
    private final QProgramTraining pt = QProgramTraining.programTraining;
    private final QTraining t = QTraining.training;
    private final QUser u = QUser.user;

    public List<UserTrainingItemResponse> findUserTrainings(
        Long userId,
        UserTrainingStatus userTrainingStatus,  // PENDING / IN_PROGRESS / COMPLETED
        TrainingType trainingType               // ONLINE / OFFLINE
    ) {

        BooleanBuilder where = new BooleanBuilder()
            .and(ut.user.userId.eq(userId));

        if (userTrainingStatus != null) {
            where.and(ut.status.eq(userTrainingStatus));
        }

        if (trainingType != null) {
            where.and(t.type.eq(trainingType));
        }

        return query
            .select(Projections.constructor(
                UserTrainingItemResponse.class,
                t.trainingId,
                t.title,
                t.type.stringValue(),
                t.onlineUrl,
                t.description,

                ut.status.stringValue(),
                ut.score,
                ut.completedAt,
                ut.createdAt,
                ut.updatedAt,

                pt.scheduledAt,
                pt.startDate,
                pt.endDate
            ))
            .from(ut)
            .join(ut.programTraining, pt)
            .join(pt.training, t)
            .where(where)
            .fetch();
    }

    public Optional<UserTrainingDetailResponse> findUserTrainingDetail(Long userId,
        Long trainingId) {

        QFormResult fr = QFormResult.formResult;

        return Optional.ofNullable(
            query
                .select(
                    Projections.fields(
                        UserTrainingDetailResponse.class,

                        ut.userTrainingId.as("userTrainingId"),
                        ut.user.userId.as("userId"),
                        t.trainingId.as("trainingId"),

                        t.title,
                        t.type,
                        t.onlineUrl,
                        t.description,

                        pt.startDate,
                        pt.endDate,
                        pt.scheduledAt,

                        ut.status,
                        ut.completedAt,
                        ut.createdAt,
                        ut.updatedAt,

                        fr.score,
                        fr.maxScore,
                        fr.passingScore,
                        fr.result,
                        fr.submittedAt
                    )
                )
                .from(ut)
                .join(ut.programTraining, pt)
                .join(pt.training, t)
                .leftJoin(fr)
                .on(
                    fr.user.userId.eq(userId)
                        .and(fr.programTraining.training.trainingId.eq(trainingId))
                        .and(fr.isDeleted.eq(false))
                )
                .where(
                    ut.user.userId.eq(userId)
                        .and(pt.training.trainingId.eq(trainingId))
                )
                .fetchOne()
        );
    }

    public Optional<UserTraining> findEntity(Long userId, Long trainingId) {

        return Optional.ofNullable(
            query.select(ut)
                .from(ut)
                .join(ut.programTraining, pt).fetchJoin()
                .join(pt.training, t).fetchJoin()
                .where(
                    ut.user.userId.eq(userId)
                        .and(t.trainingId.eq(trainingId))
                )
                .fetchOne()
        );
    }

    @Override
    public List<UserTraining> findOverdueTrainings(LocalDate today) {

        return query
            .selectFrom(ut)
            .join(ut.programTraining, pt).fetchJoin()
            .where(
                ut.status.notIn(UserTrainingStatus.COMPLETED, UserTrainingStatus.MISSED),
                pt.endDate.isNotNull(),
                pt.endDate.before(today)
            )
            .fetch();
    }

    @Override
    public List<UserTraining> findOnlineDueAt(LocalDate targetDate) {

        return query
            .selectFrom(ut)
            .join(ut.programTraining, pt).fetchJoin()
            .join(pt.training, t).fetchJoin()
            .join(ut.user, u).fetchJoin()
            .where(
                t.type.eq(TrainingType.ONLINE),
                pt.endDate.eq(targetDate),
                ut.status.notIn(UserTrainingStatus.COMPLETED, UserTrainingStatus.MISSED),
                u.role.eq(Role.USER)
            )
            .fetch();
    }

    @Override
    public List<UserTraining> findOfflineScheduledAt(LocalDate targetDate) {

        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.atTime(LocalTime.MAX);

        return query
            .selectFrom(ut)
            .join(ut.programTraining, pt).fetchJoin()
            .join(pt.training, t).fetchJoin()
            .join(ut.user, u).fetchJoin()
            .where(
                t.type.eq(TrainingType.OFFLINE),
                ut.status.notIn(UserTrainingStatus.COMPLETED, UserTrainingStatus.MISSED),
                pt.scheduledAt.between(startOfDay, endOfDay),
                u.role.eq(Role.USER)
            )
            .fetch();
    }

    @Override
    public List<UserTraining> findOfflineFormPendingAt(LocalDate targetDate) {

        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.atTime(LocalTime.MAX);

        return query
            .selectFrom(ut)
            .join(ut.programTraining, pt).fetchJoin()
            .join(pt.training, t).fetchJoin()
            .join(ut.user, u).fetchJoin()
            .where(
                t.type.eq(TrainingType.OFFLINE),
                pt.scheduledAt.between(startOfDay, endOfDay),
                ut.status.eq(UserTrainingStatus.COMPLETED),
                ut.result.isNull(),
                u.role.eq(Role.USER)
            )
            .fetch();
    }
}

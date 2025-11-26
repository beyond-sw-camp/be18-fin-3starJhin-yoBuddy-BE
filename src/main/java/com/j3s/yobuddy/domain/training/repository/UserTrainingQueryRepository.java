package com.j3s.yobuddy.domain.training.repository;

import com.j3s.yobuddy.domain.formresult.entity.QFormResult;
import com.j3s.yobuddy.domain.training.dto.response.UserTrainingDetailResponse;
import com.j3s.yobuddy.domain.training.dto.response.UserTrainingItemResponse;
import com.j3s.yobuddy.domain.training.entity.QProgramTraining;
import com.j3s.yobuddy.domain.training.entity.QTraining;
import com.j3s.yobuddy.domain.training.entity.QUserTraining;
import com.j3s.yobuddy.domain.training.entity.TrainingType;
import com.j3s.yobuddy.domain.training.entity.UserTraining;
import com.j3s.yobuddy.domain.training.entity.UserTrainingStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserTrainingQueryRepository {

    private final JPAQueryFactory query;

    public List<UserTrainingItemResponse> findUserTrainings(
        Long userId,
        UserTrainingStatus userTrainingStatus,  // PENDING / IN_PROGRESS / COMPLETED
        TrainingType trainingType               // ONLINE / OFFLINE
    ) {
        QUserTraining ut = QUserTraining.userTraining;
        QProgramTraining pt = QProgramTraining.programTraining;
        QTraining t = QTraining.training;

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

        QUserTraining ut = QUserTraining.userTraining;
        QProgramTraining pt = QProgramTraining.programTraining;
        QTraining t = QTraining.training;
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
        QUserTraining ut = QUserTraining.userTraining;
        QProgramTraining pt = QProgramTraining.programTraining;
        QTraining t = QTraining.training;

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
}

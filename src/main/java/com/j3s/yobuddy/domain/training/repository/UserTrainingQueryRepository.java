package com.j3s.yobuddy.domain.training.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.j3s.yobuddy.domain.training.dto.response.UserTrainingDetailResponse;
import com.j3s.yobuddy.domain.training.dto.response.UserTrainingItemResponse;
import com.j3s.yobuddy.domain.training.entity.QFormResult;
import com.j3s.yobuddy.domain.training.entity.QProgramTraining;
import com.j3s.yobuddy.domain.training.entity.QTraining;
import com.j3s.yobuddy.domain.training.entity.QUserTraining;
import com.j3s.yobuddy.domain.training.entity.TrainingType;
import com.j3s.yobuddy.domain.training.entity.UserTrainingStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

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

    public Optional<UserTrainingDetailResponse> findUserTrainingDetail(Long userId, Long trainingId) {

        QUserTraining ut = QUserTraining.userTraining;
        QProgramTraining pt = QProgramTraining.programTraining;
        QTraining t = QTraining.training;
        QFormResult fr = QFormResult.formResult;

        return Optional.ofNullable(
            query
                .select(
                    Projections.constructor(
                        UserTrainingDetailResponse.class,

                        // userId + trainingId
                        ut.user.userId,      // userId
                        t.trainingId,    // trainingId (PathVariable과 동일해야 함)

                        // Trainings 정보
                        t.title,
                        t.type,
                        t.description,
                        t.onlineUrl,

                        // Program_Trainings 일정 정보
                        pt.startDate,
                        pt.endDate,
                        pt.scheduledAt,

                        // User_Trainings 정보
                        ut.status,
                        ut.completedAt,
                        ut.createdAt,
                        ut.updatedAt,

                        // Form_Results 정보
                        fr.score,
                        fr.maxScore,
                        fr.passingScore,
                        fr.result,
                        fr.submittedAt
                    )
                )
                .from(ut)

                // User_Trainings → Program_Trainings
                .join(ut.programTraining, pt)

                // Program_Trainings → Trainings
                .join(pt.training, t)

                // Form_Results (user_id + training_id)
                .leftJoin(fr)
                .on(
                    fr.user.userId.eq(userId)
                        .and(fr.training.trainingId.eq(trainingId))
                        .and(fr.isDeleted.eq(false))
                )

                // WHERE 조건
                .where(
                    ut.user.userId.eq(userId)
                        .and(pt.training.trainingId.eq(trainingId))   // ★ 핵심: path variable과 매칭
                )

                .fetchOne()
        );
    }
}

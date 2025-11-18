package com.j3s.yobuddy.domain.training.repository;

import com.j3s.yobuddy.domain.training.dto.response.ProgramTrainingItemResponse;
import com.j3s.yobuddy.domain.training.entity.QProgramTraining;
import com.j3s.yobuddy.domain.training.entity.QTraining;
import com.j3s.yobuddy.domain.training.entity.TrainingType;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProgramTrainingQueryRepository {

    private final JPAQueryFactory query;

    public List<ProgramTrainingItemResponse> findProgramTrainings(
        Long programId,
        TrainingType type,
        boolean includeUnassigned
    ) {
        QProgramTraining pt = QProgramTraining.programTraining;
        QTraining t = QTraining.training;

        var builder = query
            .select(Projections.constructor(
                ProgramTrainingItemResponse.class,
                t.trainingId,
                t.title,
                t.type.stringValue(),
                t.description,
                pt.assignedAt,
                pt.scheduledAt,
                pt.startDate,
                pt.endDate
            ))
            .from(t);

        if (!includeUnassigned) {
            builder.leftJoin(pt).on(pt.training.trainingId.eq(t.trainingId)
                .and(pt.program.programId.eq(programId)));
            builder.where(pt.program.programId.eq(programId));
        } else {
            builder.leftJoin(pt).on(pt.training.trainingId.eq(t.trainingId));
        }

        if (type != null) {
            builder.where(t.type.eq(type));
        }

        return builder.fetch();
    }
}
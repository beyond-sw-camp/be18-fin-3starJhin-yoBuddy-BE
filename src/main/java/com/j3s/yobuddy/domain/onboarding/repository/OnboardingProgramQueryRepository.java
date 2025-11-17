package com.j3s.yobuddy.domain.onboarding.repository;

import com.j3s.yobuddy.domain.department.entity.QDepartment;
import com.j3s.yobuddy.domain.onboarding.dto.response.OnboardingProgramListResponse;
import com.j3s.yobuddy.domain.onboarding.entity.QOnboardingProgram;
import com.j3s.yobuddy.domain.programenrollment.entity.QProgramEnrollment;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OnboardingProgramQueryRepository {
    private final JPAQueryFactory query;

    public List<OnboardingProgramListResponse> findProgramList(){
        QOnboardingProgram p = QOnboardingProgram.onboardingProgram;
        QDepartment d = QDepartment.department;
        QProgramEnrollment e = QProgramEnrollment.programEnrollment;

        return query
            .select(Projections.constructor(
                OnboardingProgramListResponse.class,
                p.programId,
                p.name,
                d.name,
                p.status,
                p.startDate,
                p.endDate,

                e.enrollmentId.countDistinct().intValue(),

                Expressions.numberTemplate(
                    Double.class,
                    "CASE " +
                        "WHEN {0} IS NULL OR {1} IS NULL THEN 0 " +
                        "WHEN CURRENT_DATE < {0} THEN 0 " +
                        "WHEN CURRENT_DATE > {1} THEN 100 " +
                        "ELSE (DATEDIFF(CURRENT_DATE, {0}) / DATEDIFF({1}, {0}) * 100) " +
                        "END",
                    p.startDate,
                    p.endDate
                ),

                p.createdAt
            ))
            .from(p)
            .leftJoin(p.department, d)
            .leftJoin(p.enrollments, e)
            .where(p.deleted.eq(false))
            .groupBy(p.programId)
            .orderBy(p.startDate.desc())
            .fetch();
    }
}

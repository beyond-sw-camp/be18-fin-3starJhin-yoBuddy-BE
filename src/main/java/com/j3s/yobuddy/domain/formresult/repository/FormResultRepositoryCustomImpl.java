package com.j3s.yobuddy.domain.formresult.repository;

import com.j3s.yobuddy.domain.formresult.entity.FormResult;
import com.j3s.yobuddy.domain.formresult.entity.QFormResult;
import com.j3s.yobuddy.domain.onboarding.entity.QOnboardingProgram;
import com.j3s.yobuddy.domain.training.entity.QProgramTraining;
import com.j3s.yobuddy.domain.training.entity.QTraining;
import com.j3s.yobuddy.domain.user.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class FormResultRepositoryCustomImpl implements FormResultRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<FormResult> searchFormResults(String trainingName, String onboardingName,
        String userName,
        Pageable pageable) {
        QFormResult formResult = QFormResult.formResult;
        QProgramTraining programTraining = QProgramTraining.programTraining;
        QTraining training = QTraining.training;
        QOnboardingProgram onboardingProgram = QOnboardingProgram.onboardingProgram;
        QUser user = QUser.user;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(formResult.isDeleted.eq(false));

        // 교육명 검색
        if (StringUtils.hasText(userName) &&
            StringUtils.hasText(trainingName) &&
            StringUtils.hasText(onboardingName)) {

            BooleanBuilder orBuilder = new BooleanBuilder();

            orBuilder.or(user.name.containsIgnoreCase(userName));
            orBuilder.or(training.title.containsIgnoreCase(trainingName));
            orBuilder.or(onboardingProgram.name.containsIgnoreCase(onboardingName));

            builder.and(orBuilder);
        } else {
            if (StringUtils.hasText(userName)) {
                builder.and(user.name.containsIgnoreCase(userName));
            }
            if (StringUtils.hasText(trainingName)) {
                builder.and(training.title.containsIgnoreCase(trainingName));
            }
            if (StringUtils.hasText(onboardingName)) {
                builder.and(onboardingProgram.name.containsIgnoreCase(onboardingName));
            }
        }

        // 기본 조회 쿼리
        JPAQuery<FormResult> query = queryFactory
            .selectFrom(formResult)
            .join(formResult.programTraining, programTraining).fetchJoin()
            .join(programTraining.training, training)
            .join(programTraining.program, onboardingProgram)
            .join(formResult.user, user)
            .where(builder);

        for (Sort.Order order : pageable.getSort()) {
            switch (order.getProperty()) {
                case "createdAt" -> query.orderBy(
                    formResult.createdAt.desc()
                );

                case "trainingName" -> query.orderBy(
                    training.title.asc()
                );

                case "onboardingName" -> query.orderBy(
                    onboardingProgram.name.asc()
                );

                case "userName" -> query.orderBy(
                    user.name.asc()
                );
            }
        }
        List<FormResult> content = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = Optional.ofNullable(
            queryFactory
                .select(formResult.count())
                .from(formResult)
                .join(formResult.programTraining, programTraining)
                .join(programTraining.training, training)
                .join(programTraining.program, onboardingProgram)
                .join(formResult.user, user)
                .where(builder)
                .fetchOne()).orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }
}

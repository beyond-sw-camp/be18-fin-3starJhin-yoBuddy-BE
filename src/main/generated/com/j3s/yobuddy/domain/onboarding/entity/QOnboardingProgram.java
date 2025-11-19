package com.j3s.yobuddy.domain.onboarding.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOnboardingProgram is a Querydsl query type for OnboardingProgram
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOnboardingProgram extends EntityPathBase<OnboardingProgram> {

    private static final long serialVersionUID = 754050294L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOnboardingProgram onboardingProgram = new QOnboardingProgram("onboardingProgram");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final BooleanPath deleted = createBoolean("deleted");

    public final com.j3s.yobuddy.domain.department.entity.QDepartment department;

    public final StringPath description = createString("description");

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    public final ListPath<com.j3s.yobuddy.domain.programenrollment.entity.ProgramEnrollment, com.j3s.yobuddy.domain.programenrollment.entity.QProgramEnrollment> enrollments = this.<com.j3s.yobuddy.domain.programenrollment.entity.ProgramEnrollment, com.j3s.yobuddy.domain.programenrollment.entity.QProgramEnrollment>createList("enrollments", com.j3s.yobuddy.domain.programenrollment.entity.ProgramEnrollment.class, com.j3s.yobuddy.domain.programenrollment.entity.QProgramEnrollment.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final NumberPath<Long> programId = createNumber("programId", Long.class);

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    public final EnumPath<OnboardingProgram.ProgramStatus> status = createEnum("status", OnboardingProgram.ProgramStatus.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QOnboardingProgram(String variable) {
        this(OnboardingProgram.class, forVariable(variable), INITS);
    }

    public QOnboardingProgram(Path<? extends OnboardingProgram> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOnboardingProgram(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOnboardingProgram(PathMetadata metadata, PathInits inits) {
        this(OnboardingProgram.class, metadata, inits);
    }

    public QOnboardingProgram(Class<? extends OnboardingProgram> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.department = inits.isInitialized("department") ? new com.j3s.yobuddy.domain.department.entity.QDepartment(forProperty("department")) : null;
    }

}


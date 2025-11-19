package com.j3s.yobuddy.domain.programenrollment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProgramEnrollment is a Querydsl query type for ProgramEnrollment
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProgramEnrollment extends EntityPathBase<ProgramEnrollment> {

    private static final long serialVersionUID = -2097472450L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProgramEnrollment programEnrollment = new QProgramEnrollment("programEnrollment");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> enrolledAt = createDateTime("enrolledAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> enrollmentId = createNumber("enrollmentId", Long.class);

    public final com.j3s.yobuddy.domain.onboarding.entity.QOnboardingProgram program;

    public final EnumPath<ProgramEnrollment.EnrollmentStatus> status = createEnum("status", ProgramEnrollment.EnrollmentStatus.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final com.j3s.yobuddy.domain.user.entity.QUser user;

    public QProgramEnrollment(String variable) {
        this(ProgramEnrollment.class, forVariable(variable), INITS);
    }

    public QProgramEnrollment(Path<? extends ProgramEnrollment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProgramEnrollment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProgramEnrollment(PathMetadata metadata, PathInits inits) {
        this(ProgramEnrollment.class, metadata, inits);
    }

    public QProgramEnrollment(Class<? extends ProgramEnrollment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.program = inits.isInitialized("program") ? new com.j3s.yobuddy.domain.onboarding.entity.QOnboardingProgram(forProperty("program"), inits.get("program")) : null;
        this.user = inits.isInitialized("user") ? new com.j3s.yobuddy.domain.user.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}


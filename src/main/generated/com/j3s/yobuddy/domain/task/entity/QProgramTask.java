package com.j3s.yobuddy.domain.task.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProgramTask is a Querydsl query type for ProgramTask
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProgramTask extends EntityPathBase<ProgramTask> {

    private static final long serialVersionUID = 568899852L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProgramTask programTask = new QProgramTask("programTask");

    public final DateTimePath<java.time.LocalDateTime> assignedAt = createDateTime("assignedAt", java.time.LocalDateTime.class);

    public final DatePath<java.time.LocalDate> dueDate = createDate("dueDate", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.j3s.yobuddy.domain.onboarding.entity.QOnboardingProgram onboardingProgram;

    public final QOnboardingTask onboardingTask;

    public QProgramTask(String variable) {
        this(ProgramTask.class, forVariable(variable), INITS);
    }

    public QProgramTask(Path<? extends ProgramTask> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProgramTask(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProgramTask(PathMetadata metadata, PathInits inits) {
        this(ProgramTask.class, metadata, inits);
    }

    public QProgramTask(Class<? extends ProgramTask> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.onboardingProgram = inits.isInitialized("onboardingProgram") ? new com.j3s.yobuddy.domain.onboarding.entity.QOnboardingProgram(forProperty("onboardingProgram"), inits.get("onboardingProgram")) : null;
        this.onboardingTask = inits.isInitialized("onboardingTask") ? new QOnboardingTask(forProperty("onboardingTask")) : null;
    }

}


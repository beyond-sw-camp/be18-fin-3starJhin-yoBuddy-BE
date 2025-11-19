package com.j3s.yobuddy.domain.task.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;


/**
 * QOnboardingTask is a Querydsl query type for OnboardingTask
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOnboardingTask extends EntityPathBase<OnboardingTask> {

    private static final long serialVersionUID = 361527421L;

    public static final QOnboardingTask onboardingTask = new QOnboardingTask("onboardingTask");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final NumberPath<Integer> points = createNumber("points", Integer.class);

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QOnboardingTask(String variable) {
        super(OnboardingTask.class, forVariable(variable));
    }

    public QOnboardingTask(Path<? extends OnboardingTask> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOnboardingTask(PathMetadata metadata) {
        super(OnboardingTask.class, metadata);
    }

}


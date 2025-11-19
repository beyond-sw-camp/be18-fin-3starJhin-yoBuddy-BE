package com.j3s.yobuddy.domain.mentor.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMentorMenteeAssignment is a Querydsl query type for MentorMenteeAssignment
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMentorMenteeAssignment extends EntityPathBase<MentorMenteeAssignment> {

    private static final long serialVersionUID = -2098714235L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMentorMenteeAssignment mentorMenteeAssignment = new QMentorMenteeAssignment("mentorMenteeAssignment");

    public final BooleanPath deleted = createBoolean("deleted");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.j3s.yobuddy.domain.user.entity.QUser mentee;

    public final com.j3s.yobuddy.domain.user.entity.QUser mentor;

    public QMentorMenteeAssignment(String variable) {
        this(MentorMenteeAssignment.class, forVariable(variable), INITS);
    }

    public QMentorMenteeAssignment(Path<? extends MentorMenteeAssignment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMentorMenteeAssignment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMentorMenteeAssignment(PathMetadata metadata, PathInits inits) {
        this(MentorMenteeAssignment.class, metadata, inits);
    }

    public QMentorMenteeAssignment(Class<? extends MentorMenteeAssignment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.mentee = inits.isInitialized("mentee") ? new com.j3s.yobuddy.domain.user.entity.QUser(forProperty("mentee"), inits.get("mentee")) : null;
        this.mentor = inits.isInitialized("mentor") ? new com.j3s.yobuddy.domain.user.entity.QUser(forProperty("mentor"), inits.get("mentor")) : null;
    }

}


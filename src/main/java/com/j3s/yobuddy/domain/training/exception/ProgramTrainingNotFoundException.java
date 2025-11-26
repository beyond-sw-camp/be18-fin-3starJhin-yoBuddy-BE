package com.j3s.yobuddy.domain.training.exception;

public class ProgramTrainingNotFoundException extends RuntimeException {

    public ProgramTrainingNotFoundException(Long programId, Long trainingId) {
        super(
            "해당 프로그램의 할당된 교육을 찾을 수 없습니다.. (programId=" + programId + ", trainingId=" + trainingId
                + ")"
        );
    }
}

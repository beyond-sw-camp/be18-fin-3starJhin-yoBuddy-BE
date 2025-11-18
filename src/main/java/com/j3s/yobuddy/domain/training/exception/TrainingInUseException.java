package com.j3s.yobuddy.domain.training.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class TrainingInUseException extends BusinessException {

    public TrainingInUseException(Long trainingId, long activeProgramCount) {
        super(
            "현재 사용 중인 교육입니다. 온보딩 프로그램 매핑을 해제한 후 삭제할 수 있습니다."
                + " (trainingId=" + trainingId
                + ", activeProgramCount=" + activeProgramCount + ")",
            HttpStatus.CONFLICT
        );
    }
}

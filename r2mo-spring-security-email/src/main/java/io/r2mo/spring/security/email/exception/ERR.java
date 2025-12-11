package io.r2mo.spring.security.email.exception;

import io.r2mo.spring.common.exception.SpringE;
import org.springframework.http.HttpStatus;

/**
 * @author lang : 2025-12-06
 */
interface ERR {

    SpringE _80301 = SpringE.of(-80301).state(HttpStatus.BAD_REQUEST);
    SpringE _80302 = SpringE.of(-80302).state(HttpStatus.BAD_REQUEST);
    SpringE _80303 = SpringE.of(-80303).state(HttpStatus.INTERNAL_SERVER_ERROR);
}

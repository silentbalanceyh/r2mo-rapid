package io.r2mo.spring.security.exception;

import io.r2mo.spring.common.exception.SpringE;
import org.springframework.http.HttpStatus;

/**
 * @author lang : 2025-11-13
 */
interface ERR {
    SpringE _80240 = SpringE.of(-80240).state(HttpStatus.BAD_REQUEST);
    SpringE _80241 = SpringE.of(-80241).state(HttpStatus.BAD_REQUEST);
}

package io.r2mo.spring.weco.exception;

import io.r2mo.spring.common.exception.SpringE;
import org.springframework.http.HttpStatus;

/**
 * @author lang : 2025-12-06
 */
interface ERR {
    SpringE _80501 = SpringE.of(-80501).state(HttpStatus.INTERNAL_SERVER_ERROR);
    SpringE _80551 = SpringE.of(-80551).state(HttpStatus.INTERNAL_SERVER_ERROR);
}

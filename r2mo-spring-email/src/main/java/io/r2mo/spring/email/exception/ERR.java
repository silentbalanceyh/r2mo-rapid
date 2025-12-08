package io.r2mo.spring.email.exception;

import io.r2mo.spring.common.exception.SpringE;
import org.springframework.http.HttpStatus;

/**
 * @author lang : 2025-12-06
 */
interface ERR {

    SpringE _80320 = SpringE.of(-80320).state(HttpStatus.NOT_FOUND);
    SpringE _80321 = SpringE.of(-80321).state(HttpStatus.NOT_FOUND);
}

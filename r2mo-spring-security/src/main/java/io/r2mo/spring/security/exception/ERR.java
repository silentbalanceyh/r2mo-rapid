package io.r2mo.spring.security.exception;

import io.r2mo.spring.common.exception.SpringE;
import org.springframework.http.HttpStatus;

/**
 * @author lang : 2025-11-13
 */
interface ERR {
    SpringE _80222 = SpringE.of(-80222).state(HttpStatus.UNAUTHORIZED);

    SpringE _80240 = SpringE.of(-80240).state(HttpStatus.BAD_REQUEST);
    SpringE _80241 = SpringE.of(-80241).state(HttpStatus.BAD_REQUEST);

    SpringE _80242 = SpringE.of(-80242).state(HttpStatus.BAD_REQUEST);

    SpringE _80243 = SpringE.of(-80243).state(HttpStatus.UNAUTHORIZED);
    SpringE _80204 = SpringE.of(-80204).state(HttpStatus.UNAUTHORIZED);

    SpringE _80244 = SpringE.of(-80244).state(HttpStatus.UNAUTHORIZED);

    SpringE _80250 = SpringE.of(-80250).state(HttpStatus.UNAUTHORIZED);
}

package io.r2mo.spring.security.ldap.exception;

import io.r2mo.spring.common.exception.SpringE;
import org.springframework.http.HttpStatus;

/**
 * @author lang : 2025-12-09
 */
public interface ERR {

    SpringE _80401 = SpringE.of(-80401).state(HttpStatus.NOT_IMPLEMENTED);
    SpringE _80402 = SpringE.of(-80402).state(HttpStatus.UNAUTHORIZED);
}

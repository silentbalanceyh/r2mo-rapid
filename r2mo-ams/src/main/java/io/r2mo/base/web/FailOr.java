package io.r2mo.base.web;

import io.r2mo.typed.exception.WebException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author lang : 2025-09-03
 */
public interface FailOr {
    ;

    WebException transform(Throwable ex, HttpServletRequest request, HttpServletResponse response);
}

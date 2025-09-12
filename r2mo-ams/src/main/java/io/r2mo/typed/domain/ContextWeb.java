package io.r2mo.typed.domain;

import io.r2mo.typed.exception.web._501NotSupportException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author lang : 2025-09-12
 */
public interface ContextWeb {

    boolean webOk();

    <T> T webSession(boolean isObj);

    default HttpServletRequest webRequest() {
        throw new _501NotSupportException("[ IIAP ] 当前对象不支持 Request 提取！");
    }

    default HttpServletResponse webResponse() {
        throw new _501NotSupportException("[ IIAP ] 当前对象不支持 Response 提取！");
    }
}

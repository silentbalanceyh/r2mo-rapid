package io.r2mo.spring.security.extension.handler;

import io.r2mo.base.web.FailOr;
import io.r2mo.spring.common.exception.FailOrSpring;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.AbstractException;

/**
 * @author lang : 2025-11-11
 */
class SecurityFailure {

    static Cc<String, FailOr> CCT_FAILURE = Cc.openThread();

    static FailOr of() {
        return CCT_FAILURE.pick(FailOrSpring::new, FailOrSpring.class.getName());
    }

    /**
     * 在异常链中查找最深层（最接近原始抛出处）的 AbstractException 类型的异常。
     * 如果找到，则返回该 AbstractException 实例；
     * 否则返回传入的原始异常。
     *
     * @param authException 当前异常，通常是 Spring Security 的 AuthenticationException
     *
     * @return 链上最深层的 AbstractException 实例，或原始的 authException 实例
     */
    public static Throwable findExceptionAt(final RuntimeException authException) {
        if (authException == null) {
            return null;
        }

        Throwable deepestFound = null;
        Throwable current = authException;

        // 遍历整个异常链
        while (current != null) {
            // 检查当前 Throwable 是否是 AbstractException 的实例
            if (current instanceof final AbstractException actual) {
                // 如果是，更新 deepestFound 为当前找到的实例
                // 这样循环结束后，deepestFound 就是链中最深层的那个
                deepestFound = actual; // 假设 AbstractException 也是 AuthenticationException 的子类
            }
            // 移动到下一个 cause
            current = current.getCause();
        }

        // 如果找到了最深层的 AbstractException，返回它；否则返回原始异常
        return deepestFound != null ? deepestFound : authException;
    }
}

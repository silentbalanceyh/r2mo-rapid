package io.r2mo.spring.security.extension.handler;

import io.r2mo.typed.cc.Cc;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import java.util.function.Supplier;

/**
 * 安全响应策略执行器 (SPI)
 * <p>
 * 将响应行为拆分为两个阶段：
 * 1. 增强阶段 (enhance): 设置 Header、Status 等，不中断流程。
 * 2. 终结阶段 (commence): 写 Body 或重定向，中断流程，返回处理结果。
 *
 * @author lang
 */
public interface SecurityCommence {
    Cc<String, SecurityCommence> CC_COMMENCE = Cc.openThread();

    static SecurityCommence of(final Supplier<SecurityCommence> constructorFn) {
        return CC_COMMENCE.pick(constructorFn, String.valueOf(constructorFn.hashCode()));
    }

    /**
     * 🔍 匹配机制 (核心新增)
     * <p>
     * 判断当前请求是否归属该策略管辖。
     * 例如：OAuth2 策略只匹配 /oauth2/authorize
     *
     * @param request 请求对象
     * @return true=匹配，执行该策略；false=忽略，寻找下一个
     */
    boolean matches(HttpServletRequest request);

    /**
     * 🟢 阶段一：可持续性处理 (Sustainable)
     * <p>
     * 仅修改 Response 的属性（如添加 Header、设置 Cookie、修改状态码），
     * 绝不写入 Body 或关闭流。
     *
     * @param request       请求
     * @param response      响应
     * @param authException 异常信息
     */
    default void enhance(final HttpServletRequest request, final HttpServletResponse response,
                         final AuthenticationException authException) {
        // 默认不作为
    }

    /**
     * 🔴 阶段二：中断性处理 (Interruptive)
     * <p>
     * 执行最终的响应动作（如 JSON 序列化写入流、sendRedirect 跳转）。
     *
     * @param request       请求
     * @param response      响应
     * @param authException 异常信息
     * @return true=已处理（调用者应立即 return）；false=未处理（调用者可继续寻找下一个策略）
     */
    default boolean commence(final HttpServletRequest request, final HttpServletResponse response,
                             final AuthenticationException authException) {
        // 默认场景下不作为，且不中断，等价于空方法
        return false;
    }
}
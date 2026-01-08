package io.r2mo.spring.security.extension.handler;

import io.r2mo.spi.SPI;
import io.r2mo.spring.common.exception.SpringAbortExecutor;
import io.r2mo.typed.exception.WebException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * 智能安全入口点 (Security Entry Point Orchestrator)
 * <p>
 * 该类是整个 R2MO 安全框架处理 <b>“未登录访问 (401 Unauthorized)”</b> 的总调度中心。
 * 它不包含具体的业务逻辑，而是作为 <b>编排器</b>，按照预定义的优先级调度不同的 {@link SecurityCommence} 策略。
 * <p>
 * <b>调度优先级 (Decision Tree):</b>
 * <ol>
 * <li>🚨 <b>黑名单策略 (Internal):</b> 优先级最高。保护 API 接口，强制返回 JSON，防止误跳转。</li>
 * <li>🌍 <b>浏览器策略 (Internal):</b> 次高优先级。识别标准浏览器请求，执行 302 跳转至登录页。</li>
 * <li>🔌 <b>SPI 扩展策略 (External):</b> 补位优先级。处理特殊场景（如 OAuth2 授权端点、ApiFox 测试请求等）。</li>
 * <li>🛡️ <b>默认兜底 (Fallback):</b> 最低优先级。上述策略均未命中时，返回标准 JSON 错误。</li>
 * </ol>
 *
 * @author lang : 2025-11-11
 * @see SecurityCommence
 * @see SecurityCommenceBlacklist
 * @see SecurityCommenceHtml
 */
@Slf4j
@Component
public class SecurityEntryPoint implements AuthenticationEntryPoint {

    private final List<Consumer<HttpServletResponse>> waitFor = new ArrayList<>();
    private final List<SecurityCommence> delegateList = new ArrayList<>();

    public SecurityEntryPoint() {
        this.delegateList.addAll(SPI.findMany(SecurityCommence.class));
        log.info("[ R2MO ] 加载 SecurityCommence 扩展，共 {} 个。", this.delegateList.size());
    }

    @SafeVarargs
    public static SecurityEntryPoint of(final Consumer<HttpServletResponse>... consumers) {
        final SecurityEntryPoint instance = new SecurityEntryPoint();
        instance.waitFor.addAll(Arrays.asList(consumers));
        return instance;
    }

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
                         final AuthenticationException authException)
        throws IOException, ServletException {
        // ============================================================
        // 最高优先级：黑名单 / 不走 SPI，直接构造
        // ============================================================
        final SecurityCommence commenceBlack = SecurityCommence.of(SecurityCommenceBlacklist::new);
        if (commenceBlack.matches(request)) {
            commenceBlack.commence(request, response, authException);
            return;
        }


        // ============================================================
        // 次高优先级：智能重定向 / 不走 SPI，直接构造
        // ============================================================
        final SecurityCommence commenceHtml = SecurityCommence.of(SecurityCommenceHtml::new);
        if (commenceHtml.matches(request)) {
            commenceHtml.commence(request, response, authException);
            return;
        }


        // ============================================================
        // 阶段 A: 策略轮询 (Matches -> Enhance -> Commence)
        // ============================================================
        // 链式结构
        final List<SecurityCommence> delegates = this.getDelegates();
        for (final SecurityCommence delegate : delegates) {
            log.info("[ R2MO ] 触发 SecurityCommence 扩展：{}", delegate.getClass().getName());
            // 阶段一：增强响应 (可持续性)
            delegate.enhance(request, response, authException);
            // 阶段二：中断响应 (不可持续性)
            final boolean isEnd = delegate.commence(request, response, authException);
            if (isEnd) {
                return;
            }
        }


        // ============================================================
        // 阶段 B: 全局 Header 增强 (对应 waitFor)
        // ============================================================
        // 此时意味着：黑名单没中、不是浏览器、OAuth2也没认领。
        // 在执行最终 JSON 之前，先应用 waitFor (如 WWW-Authenticate)
        this.waitFor.forEach(consumer -> consumer.accept(response));


        // ============================================================
        // 阶段 C: 默认兜底 (JSON Fallback)
        // ============================================================
        // 转换异常
        final Throwable cause = SecurityFailure.findExceptionAt(authException);
        final WebException transform = SecurityFailure.of().transform(cause, request, response);

        // 输出 JSON
        SpringAbortExecutor.handleFailure(transform, response);
    }

    private List<SecurityCommence> getDelegates() {
        if (this.delegateList.isEmpty()) {
            this.delegateList.addAll(SPI.findMany(SecurityCommence.class));
        }
        return this.delegateList;
    }
}
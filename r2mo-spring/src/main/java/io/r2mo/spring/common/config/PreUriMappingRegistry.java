package io.r2mo.spring.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Comparator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class PreUriMappingRegistry implements ApplicationRunner {

    private final RequestMappingHandlerMapping handlerMapping;

    public PreUriMappingRegistry(@Qualifier("requestMappingHandlerMapping") final RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
        log.info("[ R2MO ] PreUriMappingRegistry 创建完成，处理器映射类: {}",
            handlerMapping != null ? handlerMapping.getClass().getName() : "null");
    }

    @Override
    public void run(final ApplicationArguments args) throws Exception {
        log.info("[ R2MO ] === 注册的 URI 列表 === ");

        if (this.handlerMapping == null) {
            log.error("RequestMappingHandlerMapping is null!");
            return;
        }

        final Map<RequestMappingInfo, HandlerMethod> handlerMethods = this.handlerMapping.getHandlerMethods();

        log.info("Total mappings found: {}", handlerMethods.size());

        if (handlerMethods.isEmpty()) {
            log.warn("No mappings found!");
            return;
        }

        // 按路径排序后打印
        handlerMethods.entrySet().stream()
            .sorted(Comparator.comparing(entry -> this.extractPathForSorting(entry.getKey())))
            .forEach(entry -> {
                try {
                    final RequestMappingInfo requestMappingInfo = entry.getKey();
                    final HandlerMethod handlerMethod = entry.getValue();
                    final String methodSignature = handlerMethod.getMethod().getDeclaringClass().getSimpleName()
                        + "." + handlerMethod.getMethod().getName();

                    // 使用 RequestMappingInfo 的 toString() 方法，它包含了所有信息
                    log.info("[ R2MO ] (Uri) \t {} <-- {}",
                        requestMappingInfo.toString(),
                        methodSignature);
                } catch (final Exception e) {
                    log.warn("Failed to print mapping for method: {}",
                        entry.getValue().getMethod().getName(), e);
                }
            });

        log.info("[ R2MO ] === URI 列表打印完成 === ");
    }

    /**
     * 提取路径用于排序
     * 从 RequestMappingInfo.toString() 中提取路径信息
     */
    private String extractPathForSorting(final RequestMappingInfo info) {
        try {
            final String infoStr = info.toString();
            // 匹配格式: {GET [/v1/test/hello]} 或 {POST [/api/users]}
            final Pattern pattern = Pattern.compile("\\[([^\\]]+)\\]");
            final Matcher matcher = pattern.matcher(infoStr);

            if (matcher.find()) {
                return matcher.group(1); // 返回路径部分
            }

            // 如果没有找到，返回完整字符串
            return infoStr;
        } catch (final Exception e) {
            return "";
        }
    }
}
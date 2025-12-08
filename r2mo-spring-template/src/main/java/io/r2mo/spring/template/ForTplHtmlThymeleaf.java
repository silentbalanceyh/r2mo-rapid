package io.r2mo.spring.template;

import io.r2mo.base.web.ForTpl;
import io.r2mo.typed.json.JObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @author lang : 2025-12-06
 */
@Component("TPL-THYMELEAF")
public class ForTplHtmlThymeleaf implements ForTpl {
    // 自动注入由 Spring Boot 配置好的 TemplateEngine
    @Autowired
    private TemplateEngine templateEngine;

    /**
     * 根据模板名称和参数渲染 Thymeleaf 模板，返回生成的 HTML 字符串。
     *
     * @param template 模板名称（相对于 src/main/resources/templates/ 目录，
     *                 不包括 .html 后缀。例如 'email-template' 对应
     *                 'src/main/resources/templates/email-template.html'）
     * @param params   包含模板变量的 Map (key-value pairs)
     *
     * @return 渲染后的 HTML 内容字符串
     */
    @Override
    public String process(final String template, final JObject params) {
        // 1. 创建 Thymeleaf 上下文对象
        final Context context = new Context();

        // 2. 将参数 params 中的所有内容放到上下文中
        params.fieldNames().forEach(field -> {
            final Object value = params.get(field);
            context.setVariable(field, value);
        });

        // 3. 使用 TemplateEngine 处理模板并返回结果字符串
        return this.templateEngine.process(template, context);
    }
}

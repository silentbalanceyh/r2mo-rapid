package io.r2mo.base.web;

import io.r2mo.typed.json.JObject;

/**
 * 带有语法的页面，底层实现无碍
 *
 * @author lang : 2025-12-06
 */
public interface ForTpl {
    /**
     * 可根据模板名称和参数生成最终的数据信息
     *
     * @param template 模板名称
     *                 - 模板名称如果是针对 Thymeleaf 的话，一般是类似于 "email/welcome" 这种格式，直接从模板中加载
     *                 - 模版名称有可能是 id 值，对应某个存储在数据库中的模板
     *                 - 模版名称也有可能是一个完整的模板内容（如果系统支持动态模板）
     *                 - 模版名称可以是 Freemarker、Thymeleaf、Velocity 等等任意一种模板引擎的模板标识
     * @param params   参数信息
     *
     * @return 最终生成的数据信息
     */
    String process(String template, JObject params);
}

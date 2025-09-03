package io.r2mo.base.web;

import io.r2mo.typed.webflow.WebState;

/**
 * @author lang : 2025-09-03
 */
public interface ForStatus {

    <T> WebState fail(T status);

    <T> WebState ok(T status);

    WebState ok();

    WebState ok204();

    /* 常用状态相关信息 */
    WebState V501();

    WebState V500();

    WebState V400();

    WebState V401();

    WebState V403();

    WebState V404();

    WebState V405();

    WebState V415();
}

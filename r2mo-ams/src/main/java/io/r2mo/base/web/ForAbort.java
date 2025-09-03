package io.r2mo.base.web;

import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.abort.FailOrJvm;

/**
 * @author lang : 2025-09-03
 */
public interface ForAbort {
    Cc<String, FailOr> CC_SKELETON = Cc.openThread();

    default FailOr failJvm() {
        return CC_SKELETON.pick(FailOrJvm::new, FailOrJvm.class.getName());
    }

    default FailOr failApp() {
        return null;
    }

    FailOr failContainer();
}

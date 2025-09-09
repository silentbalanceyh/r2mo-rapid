package io.r2mo.spring.common.webflow;

import io.r2mo.SourceReflect;
import io.r2mo.typed.service.ActOperation;
import io.r2mo.typed.service.ActResponse;
import io.r2mo.typed.webflow.R;
import io.r2mo.typed.webflow.WebResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Supplier;

/**
 * 普通编程专用 Controller
 *
 * @author lang : 2025-09-08
 */
@Slf4j
public abstract class SuperController<T> {

    protected final Class<T> entityCls;
    private final PostResponseReply reply = PostResponseReply.of();

    public SuperController() {
        this.entityCls = SourceReflect.classT0(this.getClass());
    }

    protected ActOperation<T> service() {
        // CRUD 的 Controller 才需要此 service
        return null;
    }

    protected <RESP extends WebResponse<T>> R<List<RESP>> replyList(
        final ActResponse<List<T>> actionResult, final Supplier<RESP> supplier) {
        return this.reply.replyList(actionResult, supplier);
    }

    protected <RESP extends WebResponse<T>> R<RESP> replySuccess(
        final ActResponse<T> actionResult, final Supplier<RESP> supplier) {
        return this.reply.replySuccess(actionResult, supplier);
    }

    protected R<T> replySuccess(final ActResponse<T> actionResult) {
        return this.reply.replySuccess(actionResult);
    }

    protected R<Boolean> replySuccessOr(final ActResponse<Boolean> actionResult) {
        return this.reply.replySuccessOr(actionResult);
    }
}

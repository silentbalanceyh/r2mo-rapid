package io.r2mo.spring.common.webflow;

import io.r2mo.SourceReflect;
import io.r2mo.spi.SPI;
import io.r2mo.spring.common.webflow.attachment.FactoryMultipart;
import io.r2mo.spring.common.webflow.attachment.MultipartMeta;
import io.r2mo.spring.common.webflow.attachment.MultipartProcessor;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.service.ActOperation;
import io.r2mo.typed.service.ActResponse;
import io.r2mo.typed.webflow.R;
import io.r2mo.typed.webflow.WebResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 普通编程专用 Controller
 *
 * @author lang : 2025-09-08
 */
@Slf4j
public abstract class SuperController<T> {

    private static FactoryMultipart FACTORY_MULTIPART;
    protected final Class<T> entityCls;
    private final PostResponseReply reply = PostResponseReply.of();

    public SuperController() {
        this.entityCls = SourceReflect.classT0(this.getClass());
    }

    private FactoryMultipart multipartFactory() {
        if (Objects.nonNull(FACTORY_MULTIPART)) {
            return FACTORY_MULTIPART;
        }
        FACTORY_MULTIPART = SPI.findOne(FactoryMultipart.class,
            FactoryMultipart.SPID_DEFAULT_MULTIPART);
        return FACTORY_MULTIPART;
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

    protected MultipartProcessor<T> multipartProcessor() {
        final FactoryMultipart factoryMultipart = this.multipartFactory();
        return factoryMultipart.mediaProcessor(this.entityCls);
    }

    protected MultipartMeta<T> multipartMeta(
        final JObject config, final boolean whenImport) {
        final FactoryMultipart factoryMultipart = this.multipartFactory();
        final Map<String, Object> configMap = new HashMap<>();
        if (whenImport) {
            // TODO: 导入参数提取
        } else {
            // TODO: 导出参数提取
        }
        return factoryMultipart.mediaMeta(this.entityCls)
            .configure(configMap);
    }
}

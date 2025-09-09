package io.r2mo.spring.excel.spi;

import io.r2mo.spring.common.webflow.attachment.FactoryMultipart;
import io.r2mo.spring.common.webflow.attachment.MultipartMeta;
import io.r2mo.spring.common.webflow.attachment.MultipartProcessor;
import io.r2mo.typed.cc.Cc;

/**
 * @author lang : 2025-09-09
 */
public class ExcelFactory implements FactoryMultipart {

    private final Cc<String, MultipartProcessor<?>> CCT_PROCESSOR = Cc.openThread();

    @Override
    @SuppressWarnings("unchecked")
    public <T> MultipartProcessor<T> mediaProcessor(final Class<T> cls) {
        return (MultipartProcessor<T>) CCT_PROCESSOR.pick(() -> new ExcelProcessor<>(cls), cls.getName());
    }

    @Override
    public <T> MultipartMeta<T> mediaMeta(final Class<T> cls) {
        return new ExcelMeta<>(cls);
    }
}

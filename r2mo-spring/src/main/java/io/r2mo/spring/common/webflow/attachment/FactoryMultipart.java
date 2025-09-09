package io.r2mo.spring.common.webflow.attachment;

/**
 * @author lang : 2025-09-09
 */
public interface FactoryMultipart {

    String SPID_DEFAULT_MULTIPART = "spi.multipart.default";

    <T> MultipartProcessor<T> mediaProcessor(Class<T> cls);

    <T> MultipartMeta<T> mediaMeta(Class<T> cls);
}

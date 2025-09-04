package io.r2mo.base.generator;

/**
 * @author lang : 2025-09-04
 */
public interface GenField {

    String generateReq(Class<?> entity, GenConfig config);

    String generateResp(Class<?> entity, GenConfig config);

    String generateEnum(Class<?> entity);
}

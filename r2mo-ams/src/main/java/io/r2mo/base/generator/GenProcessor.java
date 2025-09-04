package io.r2mo.base.generator;


/**
 * @author lang : 2025-07-28
 */
public interface GenProcessor {

    void generate(Class<?> entity, GenConfig config);

    default GenField getFieldProcessor(){
        return null;
    }
}

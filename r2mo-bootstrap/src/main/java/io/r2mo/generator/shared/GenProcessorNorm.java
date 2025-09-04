package io.r2mo.generator.shared;

import io.r2mo.base.generator.GenConfig;
import io.r2mo.base.generator.GenProcessor;

/**
 * @author lang : 2025-09-04
 */
public class GenProcessorNorm implements GenProcessor {
    private final GenProcessor serviceV1 = new GenProcessorServiceV1();

    private final GenProcessor io = new GenProcessorIo();

    private final GenProcessor controller = new GenProcessorController();
    @Override
    public void generate(final Class<?> entity, final GenConfig config) {
        this.serviceV1.generate(entity, config);

        this.io.generate(entity, config);

        this.controller.generate(entity, config);
    }
}

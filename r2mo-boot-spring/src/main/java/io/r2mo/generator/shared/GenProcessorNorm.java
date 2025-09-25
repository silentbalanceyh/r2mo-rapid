package io.r2mo.generator.shared;

import io.r2mo.base.generator.GenConfig;
import io.r2mo.base.generator.GenProcessor;
import io.r2mo.typed.annotation.GenSkip;

import java.util.Objects;

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

        final GenSkip skip = entity.getDeclaredAnnotation(GenSkip.class);
        if (Objects.nonNull(skip)) {
            return;
        }

        this.io.generate(entity, config);

        this.controller.generate(entity, config);
    }
}

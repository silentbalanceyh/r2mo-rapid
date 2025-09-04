package io.r2mo.generator;

import io.r2mo.base.generator.GenConfig;
import io.r2mo.base.generator.GenMeta;
import io.r2mo.base.generator.GenProcessor;
import io.r2mo.function.Fn;
import io.r2mo.generator.shared.GenProcessorNorm;
import io.r2mo.spi.SPI;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

/**
 * SQL 脚本生成器，根据 Entity 定义生成脚本，主要脚本包含几个方面
 * <pre>
 *     1. SQL 数据库基础脚本
 *     2. Mapper 中的 interface 和 xml 文件
 * </pre>
 *
 * @author lang : 2025-07-25
 */
@Slf4j
public class SourceGenerator {

    private final GenProcessor processor;
    private final GenProcessor normalizer;
    private GenConfig genConfig;

    public SourceGenerator(final Class<? extends GenConfig> clazz) {
        this.normalizer = new GenProcessorNorm();
        try {
            this.genConfig = clazz.getDeclaredConstructor().newInstance();
        } catch (final Exception e) {
            log.error(e.getMessage());
            this.genConfig = null;
        }
        Objects.requireNonNull(this.genConfig);
        final String name = this.genConfig.getMetadata().getSpi();
        this.processor = SPI.findOne(GenProcessor.class, name);
        if (Objects.isNull(this.processor)) {
            log.error("[ R2MO ] 请选择正确的代码生成器：");
            return;
        }
        final Class<?> generatorClass = this.processor.getClass();
        log.info("[ R2MO ] 代码生成器: {}", generatorClass.getName());
    }

    public void generate() {
        // 清理上次生成
        this.purgeSql();

        final List<Class<?>> entities = this.genConfig.getEntities();
        // Mapper 生成
        for (final Class<?> entity : entities) {
            this.processor.generate(entity, this.genConfig);
            if (this.isLock(entity)) {
                continue;
            }
            // 上层生成
            this.normalizer.generate(entity, this.genConfig);

            this.writeLock(entity);
        }
    }

    public void generate(final Class<?> entity){
        if (this.isLock(entity)) {
            return;
        }
        // 上层生成
        this.normalizer.generate(entity, this.genConfig);

        this.writeLock(entity);
    }

    private boolean isLock(final Class<?> entity) {
        final Path lockFile = this.getLock(entity);
        if (Files.exists(lockFile)) {
            log.warn("[ R2MO ] 实体 {} 已锁定，跳过生成", entity.getName());
            return true;
        }
        return false;
    }

    private void writeLock(final Class<?> entity) {
        final Path lockFile = this.getLock(entity);
        if (!Files.exists(lockFile)) {
            Fn.jvmAt(() -> Files.createFile(lockFile));
        }
    }

    private Path getLock(final Class<?> entity) {
        final Path path = Paths.get("generated");
        Fn.jvmAt(() -> Files.createDirectories(path));
        return path.resolve(entity.getName() + ".lock");
    }

    @SuppressWarnings("all")
    private void purgeSql() {
        final Path pathDB = this.genConfig.outSql();
        final Path pathSchema = pathDB.resolve("schema");
        final GenMeta meta = this.genConfig.getMetadata();
        final Path v1_init_schema = pathSchema.resolve(meta.getSchema());
        try {
            if (Files.exists(v1_init_schema)) {
                Files.delete(v1_init_schema);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
}

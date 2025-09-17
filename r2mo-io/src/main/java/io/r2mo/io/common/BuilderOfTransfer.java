package io.r2mo.io.common;

import io.r2mo.io.enums.TransferOf;
import io.r2mo.io.modeling.TransferParameter;
import io.r2mo.io.modeling.TransferRequest;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.domain.builder.BuilderOf;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * 创建 TransferRequest 对象的构建器
 *
 * @author lang : 2025-09-16
 */
@Slf4j
class BuilderOfTransfer extends AbstractBuilderPre {
    static final Cc<String, BuilderPre<TransferRequest>> CCT_PRE = Cc.openThread();
    private static final Cc<String, BuilderOfTransfer> CCT_BUILDER = Cc.openThread();
    private static final ConcurrentMap<TransferOf, Supplier<BuilderPre<TransferRequest>>> BUILDER_CONSTRUCTORS =
        new ConcurrentHashMap<>() {
            {
                this.put(TransferOf.SERVICE_FILE, BuilderPreFile::new);
                this.put(TransferOf.SERVICE_DIRECTORY, BuilderPreDirectory::new);
                this.put(TransferOf.SERVICE_LARGE, BuilderPreLarge::new);
                this.put(TransferOf.SERVICE_TOKEN, BuilderPreToken::new);
            }
        };

    protected BuilderOfTransfer() {
    }

    static BuilderOf<TransferRequest> of() {
        return CCT_BUILDER.pick(BuilderOfTransfer::new, BuilderOfTransfer.class.getName());
    }

    @Override
    public <R> TransferRequest create(final R source) {
        if (source instanceof final TransferParameter parameter) {
            final TransferOf transferOf = parameter.transferOf();
            final Supplier<BuilderPre<TransferRequest>> constructorFn = BUILDER_CONSTRUCTORS.get(transferOf);
            log.info("[ R2MO ] 创建传输请求构建器: {}", transferOf.name());
            return CCT_PRE.pick(constructorFn, transferOf.name()).waitFor(parameter);
        }
        return null;
    }
}

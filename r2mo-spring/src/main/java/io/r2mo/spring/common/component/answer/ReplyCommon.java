package io.r2mo.spring.common.component.answer;

import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Binary;
import io.r2mo.typed.exception.web._415MediaNotSupportException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author lang : 2025-09-23
 */
@Slf4j
class ReplyCommon implements Reply {
    private static final Cc<String, Reply> CCT_REPLY = Cc.openThread();

    @Override
    public boolean write(final Binary binary, final HttpServletResponse response) {
        // 数据检查
        if (Objects.isNull(binary) || Objects.isNull(binary.stream())) {
            log.error("[ R2MO ] Binary 为空，无法响应");
            return false;
        }

        // Mime 支持检查
        if (!MIME.SUPPORTED.contains(binary.mime())) {
            log.error("[ R2MO ] 不支持的 Mime 类型: {}", binary.mime());
            throw new _415MediaNotSupportException(binary.mime());
        }

        // Mime 检查
        final Supplier<Reply> constructorFn = ReplyTool.SUPPLIERS.get(binary.mime());
        if (Objects.isNull(constructorFn)) {
            throw new _415MediaNotSupportException(binary.mime());
        }
        final Reply reply = CCT_REPLY.pick(constructorFn, binary.mime());
        return reply.write(binary, response);
    }
}

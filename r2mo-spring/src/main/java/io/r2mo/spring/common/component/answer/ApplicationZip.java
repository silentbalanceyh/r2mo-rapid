package io.r2mo.spring.common.component.answer;

import io.r2mo.function.Fn;
import io.r2mo.typed.common.Binary;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;

/**
 * @author lang : 2025-09-23
 */
class ApplicationZip implements Reply {

    @Override
    public boolean write(final Binary binary, final HttpServletResponse response) {
        // 设置 Zip 相关的响应头
        response.setContentType(MIME.APPLICATION_ZIP);
        response.setHeader(HttpHeaders.CONTENT_TYPE, MIME.APPLICATION_ZIP);


        // 追加文件名
        ReplyTool.onAttachment(binary, response, true);


        // 设置长度
        ReplyTool.onLength(binary, response);


        // 缓冲区大小设置
        ReplyTool.writeStream(binary.stream(), Fn.jvmOr(response::getOutputStream));
        return true;
    }
}

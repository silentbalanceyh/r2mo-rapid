package io.r2mo.spring.common.component.answer;

import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Binary;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.util.Set;

/**
 * 响应处理器，根据 Mime 选择不同的响应处理器
 *
 * @author lang : 2025-09-23
 */
public interface Reply {

    int BUFFER_SIZE = 8192;

    Cc<String, Reply> CCT_SKELETON = Cc.openThread();

    static Reply of() {
        return CCT_SKELETON.pick(ReplyCommon::new, ReplyCommon.class.getName());
    }

    /**
     * 响应处理
     *
     * @param binary   二进制数据
     * @param response 响应对象
     */
    boolean write(Binary binary, HttpServletResponse response);

    interface MIME {
        String APPLICATION_ZIP = "application/zip";
        String APPLICATION_JSON = MediaType.APPLICATION_JSON_VALUE;
        String APPLICATION_OCTET_STREAM = MediaType.APPLICATION_OCTET_STREAM_VALUE;

        Set<String> SUPPORTED = Set.of(
            APPLICATION_ZIP,            // zip 压缩包
            APPLICATION_JSON,           // JSON 数据
            APPLICATION_OCTET_STREAM    // 二进制流
        );
    }
}

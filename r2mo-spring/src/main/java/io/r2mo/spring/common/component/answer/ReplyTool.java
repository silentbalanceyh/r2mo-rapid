package io.r2mo.spring.common.component.answer;

import io.r2mo.function.Fn;
import io.r2mo.typed.common.Binary;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * @author lang : 2025-09-23
 */
class ReplyTool {

    static final ConcurrentMap<String, Supplier<Reply>> SUPPLIERS = new ConcurrentHashMap<>() {
        {
            this.put(Reply.MIME.APPLICATION_ZIP, ApplicationZip::new);
            this.put(Reply.MIME.APPLICATION_JSON, ApplicationJson::new);
            this.put(Reply.MIME.APPLICATION_OCTET_STREAM, ApplicationOctetStream::new);
        }
    };

    static final ConcurrentMap<String, String> FILE_EXTENSION = new ConcurrentHashMap<>() {
        {
            this.put(Reply.MIME.APPLICATION_ZIP, "zip");
            this.put(Reply.MIME.APPLICATION_JSON, "json");
            this.put(Reply.MIME.APPLICATION_OCTET_STREAM, "dat");
        }
    };

    static void onLength(final Binary binary, final HttpServletResponse response) {
        if (0 < binary.length()) {
            response.setContentLengthLong(binary.length());
        }
    }

    static void onAttachment(final Binary binary, final HttpServletResponse response, final boolean force) {
        if (!force && (Objects.isNull(binary.filename()) || binary.filename().isEmpty())) {
            // 不强制设置的情况下，没有文件名则直接不设置附件头
            return;
        }
        // force = true 强制设置附件头
        String filename = binary.filename();
        if (filename == null || filename.isEmpty()) {
            // 使用提供的文件名
            final String mime = binary.mime();
            final String ext = FILE_EXTENSION.getOrDefault(mime, "dat");
            filename = "file." + ext;
        }
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + filename + "\"");
    }

    static void writeText(final Reader reader, final Writer writer) {
        writeText(reader, writer, Reply.BUFFER_SIZE);
    }

    @SuppressWarnings("all")
    static void writeText(final Reader reader, final Writer writer, final int bufferSize) {
        if (reader == null || writer == null) {
            throw new IllegalArgumentException("[ R2MO ] Reader和Writer不能为空");
        }

        final char[] buffer = new char[bufferSize];
        Fn.jvmAt(() -> {
            int charsRead;

            while ((charsRead = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, charsRead);
            }

            writer.flush();
        });
    }

    static void writeStream(final InputStream input, final OutputStream output) {
        writeStream(input, output, Reply.BUFFER_SIZE);
    }

    @SuppressWarnings("all")
    static void writeStream(final InputStream input, final OutputStream output, final int bufferSize) {
        if (input == null || output == null) {
            throw new IllegalArgumentException("[ R2MO ] 输入流和输出流不能为空");
        }

        final byte[] buffer = new byte[bufferSize];
        Fn.jvmAt(() -> {
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            output.flush();
        });
    }
}

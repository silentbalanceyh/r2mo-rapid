package io.r2mo.spring.common.webflow.attachment;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * @author lang : 2025-09-04
 */
public interface MultipartProcessor<T> {

    default T toOne(final MultipartFile file, final MultipartMeta<T> config) {
        return null;
    }

    default List<T> toMany(final MultipartFile files, final MultipartMeta<T> config) {
        return List.of();
    }

    default InputStream toBinary(final List<T> entities, final MultipartMeta<T> config) {
        return null;
    }
}

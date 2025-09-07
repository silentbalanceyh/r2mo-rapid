package io.r2mo.spring.common.webflow;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * @author lang : 2025-09-04
 */
public interface BaseAttachment<T> {
    
    default T toOne(final MultipartFile file) {
        return null;
    }

    default List<T> toMany(final MultipartFile files) {
        return List.of();
    }

    default InputStream toBinary(final List<T> entities) {
        return null;
    }
}

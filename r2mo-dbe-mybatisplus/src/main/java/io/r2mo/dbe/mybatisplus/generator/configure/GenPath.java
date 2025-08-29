package io.r2mo.dbe.mybatisplus.generator.configure;

import io.r2mo.dbe.mybatisplus.generator.SourceStructure;

import java.nio.file.Path;

/**
 * @author lang : 2025-08-29
 */
public interface GenPath {

    static GenPath of(final SourceStructure source) {
        return switch (source) {
            case DPA -> new GenPathDPA();
            case ONE -> new GenPathOne();
        };
    }

    Path outDao();

    Path outService();

    Path outApi();

    Path outSchema();
}

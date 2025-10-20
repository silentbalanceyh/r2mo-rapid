package io.r2mo.vertx.jooq.generate;

import org.jooq.codegen.JavaWriter;

import java.io.File;

/**
 * Exposes ref-methods from JavaWriter
 */
public class VertxJavaWriter extends JavaWriter {


    public VertxJavaWriter(final File file, final String fullyQualifiedTypes, final String encoding) {
        super(file, fullyQualifiedTypes, encoding);
    }


    @Override
    protected String beforeClose(final String string) {
        return super.beforeClose(string);
    }


    @Override
    public String ref(final String clazzOrId, final int keepSegments) {
        return super.ref(clazzOrId, keepSegments);
    }

    @Override
    public String ref(final String clazz) {
        return super.ref(clazz);
    }
}

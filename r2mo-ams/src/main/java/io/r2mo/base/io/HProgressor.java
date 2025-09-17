package io.r2mo.base.io;

/**
 * @author lang : 2025-09-18
 */
@FunctionalInterface
public interface HProgressor {
    void onProgress(long bytesWritten);

    default void onComplete(final long totalBytes) {
    }

    default void onError(final Exception error) {
    }
}

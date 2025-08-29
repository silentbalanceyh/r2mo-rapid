package io.r2mo.base.io;

/**
 * @author lang : 2025-08-28
 */
public interface HStore {

    HDirectory ofDirectory();

    HFile ofFile();
}

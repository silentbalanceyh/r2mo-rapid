package io.r2mo.io.local.operation;

import io.r2mo.io.common.AbstractHStore;

import java.time.LocalDateTime;

/**
 * @author lang : 2025-09-21
 */
abstract class HStoreLocalMeta extends AbstractHStore {
    @Override
    public String metaMD5(final String path) {
        return MetaCommon.metaChecksum(path, "MD5");
    }

    @Override
    public String metaChecksum(final String path) {
        return MetaCommon.metaChecksum(path, "SHA-256");
    }

    @Override
    public LocalDateTime metaModifiedAt(final String path) {
        return MetaCommon.metaModifiedAt(path);
    }

    @Override
    public String fileDirectory(final String path) {
        return MetaFile.fileDirectory(path);
    }

    @Override
    public String fileMime(final String path) {
        return MetaFile.fileMime(path);
    }

    @Override
    public long fileSize(final String path) {
        return MetaFile.fileSize(path);
    }

    @Override
    public String fileExtension(final String path) {
        return MetaFile.fileExtension(path);
    }

    @Override
    public String fileName(final String path) {
        return MetaFile.fileName(path);
    }

    @Override
    public boolean isHidden(final String path) {
        return MetaIs.isHidden(path);
    }

    @Override
    public boolean isReadOnly(final String path) {
        return MetaIs.isReadOnly(path);
    }

    @Override
    public boolean isExist(final String path) {
        return MetaIs.isExist(path);
    }

    @Override
    public boolean isDirectory(final String path) {
        return MetaIs.isDirectory(path);
    }

    @Override
    public boolean isFile(final String path) {
        return MetaIs.isFile(path);
    }

    @Override
    public boolean isEmpty(final String path) {
        return MetaIs.isEmpty(path);
    }

    @Override
    public boolean isSame(final String path1, final String path2) {
        return MetaCompare.isSame(path1, path2);
    }
}

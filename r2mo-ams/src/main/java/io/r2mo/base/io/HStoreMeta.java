package io.r2mo.base.io;

import java.time.LocalDateTime;

/**
 * @author lang : 2025-09-21
 */
interface HStoreMeta {

    // ================= Metadata 系列信息
    // ----------------- 判断函数

    boolean isExist(String path);

    boolean isDirectory(String path);

    boolean isFile(String path);

    boolean isEmpty(String path);

    boolean isSame(String path1, String path2);

    boolean isReadOnly(String path);

    boolean isHidden(String path);

    // ----------------- 文件信息
    String fileName(String path);

    String fileExtension(String path);

    long fileSize(String path);

    String fileMime(String path);

    String fileDirectory(String path);

    // ----------------- 更改信息
    LocalDateTime metaModifiedAt(String path);

    String metaChecksum(String path);

    String metaMD5(String path);
}

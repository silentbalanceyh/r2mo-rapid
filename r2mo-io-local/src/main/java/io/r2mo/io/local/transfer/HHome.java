package io.r2mo.io.local.transfer;

/**
 * 执行一个 SPI 让外层提供本地存储的根目录，此处要根据根目录来执行上传下载，工具类所需
 *
 * @author lang : 2025-09-18
 */
public interface HHome {

    String ioHome();
}

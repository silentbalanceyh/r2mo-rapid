package io.r2mo.base.io;

import cn.hutool.core.io.IoUtil;
import io.r2mo.function.Fn;
import io.r2mo.typed.common.Binary;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.r2mo.typed.json.JBase;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * 存储操作专用接口，操作主要分两部分
 * <pre>
 *     1. 目录操作
 *     2. 文件操作
 * </pre>
 * 根据不同实现会分为本地模式、网络模式，其中网络模式会包含 FTP、SFTP、HDFS 等等，可实现分布式存储管理
 *
 * @author lang : 2025-08-28
 */
public interface HStore extends Serializable {

    String DEFAULT_ID = "spi.io.store.DEFAULT";

    /* 提取根目录 */
    String pHome();

    /* 拷贝：cp */
    boolean cp(String name, String renamed);

    /* 创建目录：mkdir -p */
    boolean mkdir(String dir);

    boolean mkdir(Set<String> dirs);

    /* 重命名：mv */
    boolean mv(String from, String to);

    boolean mv(ConcurrentMap<String, String> vectorMap);

    /* 删除：rm */
    boolean rm(String filename);

    boolean rm(Set<String> filenameSet);

    /* 目录枚举：ls */

    List<String> lsFiles(String filename, String keyword);

    List<String> lsFilesN(String filename, String keyword);

    List<String> lsDirs(String filename);

    List<String> lsDirsN(String filename);

    /* 文件写入：echo */
    boolean write(String filename, String content, boolean append);

    /* 文件写入 */
    default boolean write(final String filename, final InputStream in) {
        return this.write(filename, in, null);
    }

    boolean write(String filename, InputStream in, HProgressor progress);


    boolean isExist(String path);

    boolean isDirectory(String path);

    boolean isFile(String path);

    boolean isEmpty(String path);

    boolean isSame(String path1, String path2);

    /**
     * 核心方法，从一个 filename 读取 URL，此路径作为读取数据和写入数据的核心桥梁方法，基本上所有的读取和写入都基于 URL 来完成，只要将
     * 一个 filename 转换为 URL，就可以实现针对它的所有读取，返回结果都统一使用 {@link InputStream} 来完成，这种模式下可以直接支持
     * 网络文件读取以及压缩文件读取，这样的方式就可以在实现上做到非常灵活并且统一做底层处理。
     *
     * @param filename 文件名
     *
     * @return URL
     */
    URL toURL(String filename);

    URL toURL(File file);

    URL toURL(Path path);

    // ------------------------------------ 接口级方法
    default List<String> lsFiles(final String filename) {
        return this.lsFiles(filename, null);
    }

    default List<String> lsFilesN(final String filename) {
        return this.lsFiles(filename, null);
    }

    default <T extends JBase> T inJson(final String filename) {
        return JBase.parse(this.inString(filename));
    }

    default <T extends JBase> T inJson(final URL url) {
        return JBase.parse(this.inString(url));
    }

    /* 文件读取 */
    // -> filename -> InputStream -> String
    default String inString(final String filename) {
        return IoUtil.read(this.inStream(filename), StandardCharsets.UTF_8);
    }

    // -> URL -> InputStream -> String
    default String inString(final URL url) {
        return IoUtil.read(this.inStream(url), StandardCharsets.UTF_8);
    }

    // -> filename -> InputStream -> byte[]
    default byte[] inBytes(final String filename) {
        return IoUtil.readBytes(this.inStream(filename));
    }

    // -> URL -> InputStream -> byte[]
    default byte[] inBytes(final URL url) {
        return IoUtil.readBytes(this.inStream(url));
    }

    /* 文件 Stream 读取 */
    // -> filename -> URL -> InputStream
    default InputStream inStream(final String filename) {
        return this.inStream(this.toURL(filename));
    }

    // -> URL -> InputStream
    default InputStream inStream(final URL url) {
        if (null == url) {
            return null;
        }
        return Fn.jvmOr(url::openStream, null);
    }

    <T extends JBase> T inYaml(URL url);

    <T extends JBase> T inYaml(String filename);

    // ---------------- 下边方法不适合在网络或分布式环境中使用
    <T extends JBase> T inYaml(File file);

    <T extends JBase> T inYaml(Path path);

    /*
     * 最终异常会从 inStream 中抛出
     */
    default <T extends JBase> T inJson(final File file) {
        return JBase.parse(this.inString(file));
    }

    default <T extends JBase> T inJson(final Path file) {
        return JBase.parse(this.inString(file));
    }

    default String inString(final File file) {
        return IoUtil.read(this.inStream(file), StandardCharsets.UTF_8);
    }

    default String inString(final Path file) {
        return IoUtil.read(this.inStream(file), StandardCharsets.UTF_8);
    }

    default byte[] inBytes(final File file) {
        return IoUtil.readBytes(this.inStream(file));
    }

    default byte[] inBytes(final Path path) {
        return IoUtil.readBytes(this.inStream(path));
    }

    default InputStream inStream(final File file) {
        throw new _501NotSupportException("[ R2MO ] 当前实现类不支持 File 类型：HStore.inStream(File)");
    }

    default InputStream inStream(final Path path) {
        throw new _501NotSupportException("[ R2MO ] 当前实现类不支持 Path 类型：HStore.inStream(Path");
    }

    default Binary inBinary(final String filename) {
        return this.inBinary(filename, null);
    }

    /* 文件读取 */
    Binary inBinary(String filename, HProgressor progress);

    // ---------------- 公私钥专用
    PrivateKey inPrivate(String filename);

    PrivateKey inPrivate(InputStream in);

    PublicKey inPublic(String filename);

    PublicKey inPublic(InputStream in);

    SecretKey inSecret(String filename);

    SecretKey inSecret(InputStream in);

    boolean write(String filename, PrivateKey key);

    boolean write(String filename, PublicKey key);

    boolean write(String filename, SecretKey key);
}

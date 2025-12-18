package io.r2mo.base.io;

import cn.hutool.core.io.IoUtil;
import io.r2mo.base.io.common.FileMem;
import io.r2mo.base.io.modeling.FileRange;
import io.r2mo.typed.common.Binary;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JBase;
import io.r2mo.typed.json.JObject;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
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
public interface HStore extends HStoreMeta, Serializable {

    String DEFAULT_ID = "spi.io.store.DEFAULT";

    /* 提取根目录 */
    String pHome();

    default String pHome(final String path) {
        Objects.requireNonNull(path, "[ R2MO ] 目录路径不能为空");
        return HUri.UT.resolve(this.pHome(), path);
    }

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

    default boolean write(final String filename, final Binary binary) {
        return this.write(filename, binary.stream(), null);
    }

    default boolean write(final String filename, final Binary binary, final HProgressor progressor) {
        return this.write(filename, binary.stream(), progressor);
    }

    default boolean write(final String filename, final byte[] data) {
        return this.write(filename, IoUtil.toStream(data), null);
    }

    boolean write(String filename, InputStream in, HProgressor progress);

    /**
     * 核心方法，从一个 filename 读取 URL，此路径作为读取数据和写入数据的核心桥梁方法，基本上所有的读取和写入都基于 URL 来完成，只要将
     * 一个 filename 转换为 URL，就可以实现针对它的所有读取，返回结果都统一使用 {@link InputStream} 来完成，这种模式下可以直接支持
     * 网络文件读取以及压缩文件读取，这样的方式就可以在实现上做到非常灵活并且统一做底层处理。此处新版的增强
     * <pre>
     *     1. 如果是本地类型 -> Local，开启多级加载策略（Chain of Responsibility）
     *     2. 多级优先级如下：
     *        - 文件系统：FileSystem：尝试作为普通文件路径加载，原来的逻辑追加存在性校验
     *        - 类路径：ClassPath：尝试从当前上下文或类的 ClassLoader 加载，包括 JAR 包内的资源
     *        - Zip/Jar协议：Zip/Jar Protocol：尝试使用 Zip 或 Jar 协议加载，适用于压缩包内资源读取
     * </pre>
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

    JObject ymlForJ(String yaml);

    JArray ymlForA(String yaml);

    /* 文件读取 */
    // -> filename -> InputStream -> String
    default String inString(final String filename) {
        return this.readSafe(this.inStream(filename), StandardCharsets.UTF_8);
    }

    // -> URL -> InputStream -> String
    default String inString(final URL url) {
        return this.readSafe(this.inStream(url), StandardCharsets.UTF_8);
    }

    // -> filename -> InputStream -> byte[]
    default byte[] inBytes(final String filename) {
        return this.readSafe(this.inStream(filename));
    }

    // -> URL -> InputStream -> byte[]
    default byte[] inBytes(final URL url) {
        return this.readSafe(this.inStream(url));
    }

    // 辅助方法：安全读取 InputStream 为 String
    @SuppressWarnings("all")
    private String readSafe(final InputStream inputStream, final Charset charset) {
        return inputStream != null ? IoUtil.read(inputStream, charset) : null;
    }

    // 辅助方法：安全读取 InputStream 为 byte[]
    private byte[] readSafe(final InputStream inputStream) {
        return inputStream != null ? IoUtil.readBytes(inputStream) : null;
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
        // 吞掉异常：java.io.FileNotFoundException: xxx (No such file or directory)
        try {
            return url.openStream();
        } catch (final FileNotFoundException e) {
            return null; // 不存在 -> 返回 null，不打印栈
        } catch (final IOException e) {
            // 其它 IO 问题也按需吞掉或打 debug
            return null;
        }
    }

    // -> filename -> InputStream -> Properties
    default Properties inProperties(final String filename) {
        return this.inProperties(this.inStream(filename));
    }

    default Properties inProperties(final URL url) {
        return this.inProperties(this.inStream(url));
    }

    Properties inProperties(InputStream in);

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
        return this.readSafe(this.inStream(file), StandardCharsets.UTF_8);
    }

    default String inString(final Path file) {
        return this.readSafe(this.inStream(file), StandardCharsets.UTF_8);
    }

    default byte[] inBytes(final File file) {
        return this.readSafe(this.inStream(file));
    }

    default byte[] inBytes(final Path path) {
        return this.readSafe(this.inStream(path));
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
    Binary inBinary(String filename, HProgressor progressRef);

    /*
     * 压缩流，只有压缩流才会存在直接读取多个文件的场景，其他场景下不可能使用某种方式将多个文件合并为一个文件
     * 流来执行相关读取，所以这种场景下依旧使用同样的函数签名，但是传入的参数变为 Set<String> 来表示多个文件
     * */
    default Binary inBinary(final Set<String> files) {
        return this.inBinary(files, Set.of(), null);
    }

    default Binary inBinary(final Set<String> files, final HProgressor progressRef) {
        return this.inBinary(files, Set.of(), progressRef);
    }

    default Binary inBinary(final Set<String> files, final Set<FileMem> memSet) {
        return this.inBinary(files, memSet, null);
    }

    Binary inBinary(Set<String> files, Set<FileMem> memSet, HProgressor progressRef);

    Binary inBinary(String filename, FileRange fileRange, HProgressor progressorRef);

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


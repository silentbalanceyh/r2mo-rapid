package io.r2mo.io.common;

import io.r2mo.base.io.HStore;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.json.JBase;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * 统一访问接口，用来完成和 IO 直接对接的部分，而 Zero 中实现的是异步模式的继承，来完成整体对接部分，替换原始的 HFS
 * 的 IO 操作
 *
 * @author lang : 2025-09-01
 */
public class HFS {
    private static final Cc<String, HFS> CCT_HFS = Cc.openThread();
    private final HStore store;

    protected HFS() {
        this.store = SPI.SPI_IO.ioAction();
    }

    public static HFS of() {
        return CCT_HFS.pick(HFS::new);
    }

    /* cp */
    public boolean cp(final String from, final String to) {
        return this.store.cp(from, to);
    }

    /* mv */
    public boolean mv(final String from, final String to) {
        return this.store.mv(from, to);
    }

    public boolean mv(final ConcurrentMap<String, String> vectorMap) {
        return this.store.mv(vectorMap);
    }

    /* mkdir -p */
    public boolean mkdir(final String dir) {
        return this.store.mkdir(dir);
    }

    public boolean mkdir(final Set<String> dirs) {
        return this.store.mkdir(dirs);
    }

    /* rm */
    public boolean rm(final String filename) {
        return this.store.rm(filename);
    }

    public boolean rm(final Set<String> filenames) {
        return this.store.rm(filenames);
    }

    /* ls */
    public List<String> lsFiles(final String filename, final String keyword) {
        return this.store.lsFiles(filename, keyword);
    }

    /* ls */
    public List<String> lsFilesN(final String filename, final String keyword) {
        return this.store.lsFilesN(filename, keyword);
    }

    public List<String> lsDirs(final String filename) {
        return this.store.lsDirs(filename);
    }

    public List<String> lsDirsN(final String filename) {
        return this.store.lsDirsN(filename);
    }

    /* 文件写入：echo */
    public boolean write(final String filename, final String content, final boolean append) {
        return this.store.write(filename, content, append);
    }

    // ------------------------------------ 接口级方法
    public List<String> lsFiles(final String filename) {
        return this.lsFiles(filename, null);
    }

    public List<String> lsFilesN(final String filename) {
        return this.lsFiles(filename, null);
    }

    /* 文件读取 */
    public String inString(final String filename) {
        return this.store.inString(filename);
    }

    public String inString(final URL url) {
        return this.store.inString(url);
    }

    public byte[] inBytes(final String filename) {
        return this.store.inBytes(filename);
    }

    public byte[] inBytes(final URL url) {
        return this.store.inBytes(url);
    }

    /* 文件 Stream 读取 */
    public InputStream inStream(final String filename) {
        return this.store.inStream(filename);
    }

    public InputStream inStream(final URL url) {
        return this.store.inStream(url);
    }

    public <T extends JBase> T inJson(final String filename) {
        return this.store.inJson(filename);
    }

    public <T extends JBase> T inJson(final URL url) {
        return this.store.inJson(url);
    }

    public <T extends JBase> T inYaml(final String filename) {
        return this.store.inYaml(filename);
    }

    public <T extends JBase> T inYaml(final URL url) {
        return this.store.inYaml(url);
    }

    // ---------------- 下边方法不适合在网络或分布式环境中使用
    public String inString(final File file) {
        return this.store.inString(file);
    }

    public String inString(final Path file) {
        return this.store.inString(file);
    }

    public byte[] inBytes(final File file) {
        return this.store.inBytes(file);
    }

    public byte[] inBytes(final Path path) {
        return this.store.inBytes(path);
    }

    public InputStream inStream(final File file) {
        return this.store.inStream(file);
    }

    public InputStream inStream(final Path path) {
        return this.store.inStream(path);
    }

    public <T extends JBase> T inJson(final File file) {
        return this.store.inJson(file);
    }

    public <T extends JBase> T inJson(final Path path) {
        return this.store.inJson(path);
    }

    public <T extends JBase> T inYaml(final File file) {
        return this.store.inYaml(file);
    }

    public <T extends JBase> T inYaml(final Path path) {
        return this.store.inYaml(path);
    }
}

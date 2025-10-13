package io.r2mo.io.local.operation;

import io.r2mo.base.io.HProgressor;
import io.r2mo.base.io.HStore;
import io.r2mo.base.io.common.FileMem;
import io.r2mo.base.io.modeling.FileRange;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.common.Binary;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-09-02
 */
@SPID(HStore.DEFAULT_ID)
@Slf4j
public class HStoreLocal extends HStoreLocalMeta {

    @Override
    public String pHome() {
        return LocalHighway.findHome();
    }

    @Override
    public boolean cp(final String source, final String target) {
        return LocalWriter.copy(source, target);
    }

    @Override
    public boolean rm(final String filename) {
        return LocalWriter.rm(filename);
    }

    @Override
    public boolean rm(final Set<String> filenameSet) {
        if (filenameSet == null || filenameSet.isEmpty()) {
            return true;
        }
        return filenameSet.parallelStream().allMatch(LocalWriter::rm);
    }

    @Override
    public boolean write(final String filename, final String content, final boolean append) {
        return LocalWriter.write(filename, content, append);
    }

    @Override
    public boolean write(final String filename, final InputStream in, final HProgressor progress) {
        return LocalHighway.write(filename, in, progress);
    }

    @Override
    public Binary inBinary(final String filename, final HProgressor progress) {
        return LocalHighway.read(filename, progress);
    }

    @Override
    public Binary inBinary(final Set<String> files, final Set<FileMem> memSet, final HProgressor progress) {
        return LocalZip.inBinary(files, memSet, progress);
    }

    @Override
    public Binary inBinary(String filename, FileRange fileRange,HProgressor progressorRef) {
        return LocalRafReader.inBinary(filename,fileRange,progressorRef);
    }

    @Override
    public boolean mkdir(final String dir) {
        return LocalWriter.mkdir(dir);
    }

    @Override
    public boolean mv(final String from, final String to) {
        return LocalWriter.move(from, to);
    }

    @Override
    public boolean mv(final ConcurrentMap<String, String> vectorMap) {
        if (vectorMap == null || vectorMap.isEmpty()) {
            return true;
        }
        return vectorMap.entrySet().parallelStream().allMatch(entry -> LocalWriter.move(entry.getKey(), entry.getValue()));
    }

    @Override
    public boolean mkdir(final Set<String> dirs) {
        if (dirs == null || dirs.isEmpty()) {
            return true;
        }
        return dirs.parallelStream().allMatch(LocalWriter::mkdir);
    }

    @Override
    public InputStream inStream(final File file) {
        return this.inStream(this.toURL(file));
    }

    @Override
    public InputStream inStream(final Path path) {
        return this.inStream(this.toURL(path));
    }

    @Override
    public PrivateKey inPrivate(final String filename) {
        return this.inPrivate(this.inStream(filename));
    }

    @Override
    public PrivateKey inPrivate(final InputStream in) {
        return SecurityIn.inPrivate(in);
    }

    @Override
    public PublicKey inPublic(final String filename) {
        return this.inPublic(this.inStream(filename));
    }

    @Override
    public PublicKey inPublic(final InputStream in) {
        return SecurityIn.inPublic(in);
    }

    @Override
    public SecretKey inSecret(final String filename) {
        return this.inSecret(this.inStream(filename));
    }

    @Override
    public SecretKey inSecret(final InputStream in) {
        return SecurityIn.inSecret(in);
    }

    @Override
    public boolean write(final String filename, final PrivateKey key) {
        log.info("[ R2MO ] （非对称）写入私钥 PrivateKey 到文件：{}", filename);
        return this.write(filename, SecurityIn.inPrivate(key));
    }

    @Override
    public boolean write(final String filename, final PublicKey key) {
        log.info("[ R2MO ] （非对称）写入公钥 PublicKey 到文件：{}", filename);
        return this.write(filename, SecurityIn.inPublic(key));
    }

    @Override
    public boolean write(final String filename, final SecretKey key) {
        log.info("[ R2MO ] （对称）写入密钥 SecretKey 到文件：{}", filename);
        return this.write(filename, SecurityIn.inSecret(key));
    }

    @Override
    public URL toURL(final String filename) {
        if (null == filename || filename.isBlank()) {
            return null;
        }
        final Path path = Path.of(filename);
        return this.toURL(path);
    }

    @Override
    public List<String> lsFiles(final String filename, final String keyword) {
        return LocalReader.lsFiles(filename, keyword);
    }

    @Override
    public List<String> lsFilesN(final String filename, final String keyword) {
        return LocalReader.lsFilesN(filename, keyword);
    }

    @Override
    public List<String> lsDirs(final String filename) {
        return LocalReader.lsDirs(filename);
    }

    @Override
    public List<String> lsDirsN(final String filename) {
        return LocalReader.lsDirsN(filename);
    }
}

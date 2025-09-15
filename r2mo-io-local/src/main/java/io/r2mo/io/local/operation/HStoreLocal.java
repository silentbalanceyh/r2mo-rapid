package io.r2mo.io.local.operation;

import io.r2mo.io.common.AbstractHStore;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-09-02
 */
public class HStoreLocal extends AbstractHStore {


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
    public boolean isExist(final String path) {
        return LocalIs.isExist(path);
    }

    @Override
    public boolean isDirectory(final String path) {
        return LocalIs.isDirectory(path);
    }

    @Override
    public boolean isFile(final String path) {
        return LocalIs.isFile(path);
    }

    @Override
    public boolean isEmpty(final String path) {
        return LocalIs.isEmpty(path);
    }

    @Override
    public boolean isSame(final String path1, final String path2) {
        return LocalCompare.isSame(path1, path2);
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

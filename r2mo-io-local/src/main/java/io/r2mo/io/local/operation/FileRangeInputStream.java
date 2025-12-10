package io.r2mo.io.local.operation;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Path;

public class FileRangeInputStream extends InputStream {

    private final RandomAccessFile raf;
    private final long startPos;
    private final long endPos;
    private long currentPos;
    private long bytesRemaining;

    /**
     * 私有构造函数
     *
     * @param filePath 文件路径
     * @param start    起始位置
     * @param end      结束位置
     *
     * @throws IOException 如果文件操作失败
     */
    private FileRangeInputStream(final Path filePath, final long start, final long end) throws IOException {
        this.raf = new RandomAccessFile(filePath.toFile(), "r");
        this.startPos = start;
        this.endPos = end;
        this.currentPos = start;
        this.bytesRemaining = end - start;

        // 验证参数有效性
        this.validateParameters();

        // 定位到起始位置
        this.raf.seek(this.startPos);
    }

    /**
     * 获取大文件指定部分的输入流
     *
     * @param filePath 文件路径
     * @param start    起始位置（包含）
     * @param end      结束位置（不包含）
     *
     * @return 指定范围的输入流
     * @throws IOException 如果文件不存在或读取失败
     */
    public static InputStream getInputStream(final Path filePath, final long start, final long end) throws IOException {
        return new FileRangeInputStream(filePath, start, end);
    }

    /**
     * 验证参数有效性
     *
     * @throws IOException 如果参数无效
     */
    private void validateParameters() throws IOException {
        final long fileLength = this.raf.length();

        if (this.startPos < 0) {
            throw new IllegalArgumentException("起始位置不能为负数: " + this.startPos);
        }

        if (this.endPos < this.startPos) {
            throw new IllegalArgumentException("结束位置不能小于起始位置: " + this.endPos + " < " + this.startPos);
        }

        if (this.startPos > fileLength) {
            throw new IllegalArgumentException("起始位置超过文件长度: " + this.startPos + " > " + fileLength);
        }

        if (this.endPos > fileLength) {
            throw new IllegalArgumentException("结束位置超过文件长度: " + this.endPos + " > " + fileLength);
        }

        if (this.bytesRemaining <= 0) {
            throw new IllegalArgumentException("读取长度必须大于0: " + this.bytesRemaining);
        }
    }

    @Override
    public int read() throws IOException {
        if (this.bytesRemaining <= 0) {
            return -1;
        }

        final int result = this.raf.read();
        if (result != -1) {
            this.currentPos++;
            this.bytesRemaining--;
        }
        return result;
    }

}
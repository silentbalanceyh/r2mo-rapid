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
     * 获取大文件指定部分的输入流
     * @param filePath 文件路径
     * @param start 起始位置（包含）
     * @param end 结束位置（不包含）
     * @return 指定范围的输入流
     * @throws IOException 如果文件不存在或读取失败
     */
    public static InputStream getInputStream(Path filePath, long start, long end) throws IOException {
        return new FileRangeInputStream(filePath, start, end);
    }



    /**
     * 私有构造函数
     * @param filePath 文件路径
     * @param start 起始位置
     * @param end 结束位置
     * @throws IOException 如果文件操作失败
     */
    private FileRangeInputStream(Path filePath, long start, long end) throws IOException {
        this.raf = new RandomAccessFile(filePath.toFile(), "r");
        this.startPos = start;
        this.endPos = end;
        this.currentPos = start;
        this.bytesRemaining = end - start;
        
        // 验证参数有效性
        validateParameters();
        
        // 定位到起始位置
        raf.seek(startPos);
    }

    /**
     * 验证参数有效性
     * @throws IOException 如果参数无效
     */
    private void validateParameters() throws IOException {
        long fileLength = raf.length();
        
        if (startPos < 0) {
            throw new IllegalArgumentException("起始位置不能为负数: " + startPos);
        }
        
        if (endPos < startPos) {
            throw new IllegalArgumentException("结束位置不能小于起始位置: " + endPos + " < " + startPos);
        }
        
        if (startPos > fileLength) {
            throw new IllegalArgumentException("起始位置超过文件长度: " + startPos + " > " + fileLength);
        }
        
        if (endPos > fileLength) {
            throw new IllegalArgumentException("结束位置超过文件长度: " + endPos + " > " + fileLength);
        }
        
        if (bytesRemaining <= 0) {
            throw new IllegalArgumentException("读取长度必须大于0: " + bytesRemaining);
        }
    }

    @Override
    public int read() throws IOException {
        if (bytesRemaining <= 0) {
            return -1;
        }
        
        int result = raf.read();
        if (result != -1) {
            currentPos++;
            bytesRemaining--;
        }
        return result;
    }

}
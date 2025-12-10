package io.r2mo.io.local.transfer;

import io.r2mo.function.Fn;
import io.r2mo.typed.common.Binary;
import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * 哈希校验和计算工具类
 * 支持多种哈希算法计算
 *
 * @author lang : 2025-09-23
 */
@Slf4j
public class ChecksumUtil {

    /**
     * 计算二进制数据的哈希校验和（默认使用SHA-256算法）
     *
     * @param chunkData 二进制数据
     *
     * @return 十六进制格式的哈希值，计算失败时返回空字符串
     */
    public static String calculateChecksum(final Binary chunkData) {
        return calculateChecksum(chunkData, HashAlgorithm.SHA256);
    }

    /**
     * 计算二进制数据的哈希校验和
     *
     * @param chunkData 二进制数据
     * @param algorithm 哈希算法
     *
     * @return 十六进制格式的哈希值，计算失败时返回空字符串
     */
    public static String calculateChecksum(final Binary chunkData, final HashAlgorithm algorithm) {
        if (chunkData == null || chunkData.length() == 0) {
            log.warn("[ R2MO ] 计算哈希校验和失败：数据为空");
            return "";
        }

        return Fn.jvmOr(() -> {
            try {
                // 获取指定算法的消息摘要实例
                final MessageDigest digest = MessageDigest.getInstance(algorithm.getAlgorithmName());

                // 更新摘要数据
                digest.update(chunkData.stream().readAllBytes());

                // 计算哈希值
                final byte[] hashBytes = digest.digest();

                // 转换为十六进制字符串
                return bytesToHex(hashBytes);

            } catch (final NoSuchAlgorithmException e) {
                log.error("[ R2MO ] 不支持的哈希算法: {}", algorithm.getAlgorithmName(), e);
                return "";
            }
        }, "");
    }

    /**
     * 计算字节数组的哈希校验和
     *
     * @param data      字节数组数据
     * @param algorithm 哈希算法
     *
     * @return 十六进制格式的哈希值，计算失败时返回空字符串
     */
    public static String calculateChecksum(final byte[] data, final HashAlgorithm algorithm) {
        if (data == null || data.length == 0) {
            log.warn("[ R2MO ] 计算哈希校验和失败：数据为空");
            return "";
        }

        try {
            final MessageDigest digest = MessageDigest.getInstance(algorithm.getAlgorithmName());
            digest.update(data);
            final byte[] hashBytes = digest.digest();
            return bytesToHex(hashBytes);

        } catch (final NoSuchAlgorithmException e) {
            log.error("[ R2MO ] 不支持的哈希算法: {}", algorithm.getAlgorithmName(), e);
            return "";
        } catch (final Exception e) {
            log.error("[ R2MO ] 计算哈希校验和失败", e);
            return "";
        }
    }

    /**
     * 计算输入流的哈希校验和（适用于大文件）
     *
     * @param inputStream 输入流
     * @param algorithm   哈希算法
     *
     * @return 十六进制格式的哈希值，计算失败时返回空字符串
     */
    public static String calculateChecksum(final java.io.InputStream inputStream, final HashAlgorithm algorithm) {
        if (inputStream == null) {
            log.warn("[ R2MO ] 计算哈希校验和失败：输入流为空");
            return "";
        }

        try {
            final MessageDigest digest = MessageDigest.getInstance(algorithm.getAlgorithmName());
            final byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }

            final byte[] hashBytes = digest.digest();
            return bytesToHex(hashBytes);

        } catch (final NoSuchAlgorithmException e) {
            log.error("[ R2MO ] 不支持的哈希算法: {}", algorithm.getAlgorithmName(), e);
            return "";
        } catch (final Exception e) {
            log.error("[ R2MO ] 计算流数据哈希校验和失败", e);
            return "";
        }
    }

    /**
     * 验证数据的哈希校验和是否匹配
     *
     * @param chunkData    二进制数据
     * @param expectedHash 期望的哈希值（十六进制格式）
     * @param algorithm    哈希算法
     *
     * @return 如果哈希值匹配返回true，否则返回false
     */
    public static boolean verifyChecksum(final Binary chunkData, final String expectedHash, final HashAlgorithm algorithm) {
        if (chunkData == null || expectedHash == null || expectedHash.isEmpty()) {
            return false;
        }

        final String actualHash = calculateChecksum(chunkData, algorithm);
        return actualHash.equalsIgnoreCase(expectedHash.trim());
    }

    /**
     * 验证字节数组的哈希校验和是否匹配
     *
     * @param data         字节数组数据
     * @param expectedHash 期望的哈希值（十六进制格式）
     * @param algorithm    哈希算法
     *
     * @return 如果哈希值匹配返回true，否则返回false
     */
    public static boolean verifyChecksum(final byte[] data, final String expectedHash, final HashAlgorithm algorithm) {
        if (data == null || expectedHash == null || expectedHash.isEmpty()) {
            return false;
        }

        final String actualHash = calculateChecksum(data, algorithm);
        return actualHash.equalsIgnoreCase(expectedHash.trim());
    }

    /**
     * 验证输入流的哈希校验和是否匹配
     *
     * @param inputStream  输入流
     * @param expectedHash 期望的哈希值（十六进制格式）
     * @param algorithm    哈希算法
     *
     * @return 如果哈希值匹配返回true，否则返回false
     */
    public static boolean verifyChecksum(final java.io.InputStream inputStream, final String expectedHash, final HashAlgorithm algorithm) {
        if (inputStream == null || expectedHash == null || expectedHash.isEmpty()) {
            return false;
        }

        final String actualHash = calculateChecksum(inputStream, algorithm);
        return actualHash.equalsIgnoreCase(expectedHash.trim());
    }

    /**
     * 字节数组转换为十六进制字符串
     *
     * @param bytes 字节数组
     *
     * @return 十六进制字符串
     */
    private static String bytesToHex(final byte[] bytes) {
        if (bytes == null) {
            return "";
        }

        // 使用Java 17+的HexFormat，或者回退到传统实现
        try {
            // Java 17+ 方式
            return HexFormat.of().formatHex(bytes);
        } catch (final NoClassDefFoundError | Exception e) {
            // 兼容旧版本Java的回退实现
            return bytesToHexLegacy(bytes);
        }
    }

    /**
     * 兼容旧版本Java的十六进制转换实现
     */
    private static String bytesToHexLegacy(final byte[] bytes) {
        if (bytes == null) {
            return "";
        }

        final StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (final byte b : bytes) {
            final String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * 获取所有支持的哈希算法名称
     */
    public static String[] getSupportedAlgorithms() {
        final HashAlgorithm[] algorithms = HashAlgorithm.values();
        final String[] names = new String[algorithms.length];
        for (int i = 0; i < algorithms.length; i++) {
            names[i] = algorithms[i].getAlgorithmName();
        }
        return names;
    }

    /**
     * 检查是否支持指定的哈希算法
     */
    public static boolean isAlgorithmSupported(final String algorithmName) {
        if (algorithmName == null || algorithmName.isEmpty()) {
            return false;
        }

        try {
            MessageDigest.getInstance(algorithmName);
            return true;
        } catch (final NoSuchAlgorithmException e) {
            return false;
        }
    }

    // 支持的哈希算法
    public enum HashAlgorithm {
        MD5("MD5"),
        SHA1("SHA-1"),
        SHA256("SHA-256"),
        SHA512("SHA-512"),
        SHA3_256("SHA3-256"),
        SHA3_512("SHA3-512");

        private final String algorithmName;

        HashAlgorithm(final String algorithmName) {
            this.algorithmName = algorithmName;
        }

        public String getAlgorithmName() {
            return this.algorithmName;
        }
    }
}
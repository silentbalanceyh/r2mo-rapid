package io.r2mo.jce.common;

import io.r2mo.jce.constant.AlgHash;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * 硬件指纹生成工具
 * <pre>
 *     指纹来源（可定制）：
 *     - OS 名称 + 架构 + 版本
 *     - 主机名
 *     - 网卡 MAC 地址（取第一个可用网卡）
 *     - Java 虚拟机供应商 + 版本
 *
 *     => 组合后使用 SHA-256 哈希，得到唯一机器指纹
 * </pre>
 *
 * @author lang
 * @since 2025-09-21
 */
class HEDFinger {
    /**
     * 将 fingerString() 的 hex 输出转换为常见的冒号分隔格式。
     * <pre>
     * 例子：
     *   fingerString() -> "a1b2c3d4..."
     *   fingerHex() -> "a1:b2:c3:d4:..."
     *
     * 规则：
     *  - 清理返回值中的非十六进制字符（0-9 a-f A-F）
     *  - 若长度为奇数，前面补 '0'
     *  - 两字符为一组，小写输出，用 ':' 连接
     * </pre>
     *
     * @return 冒号分隔的十六进制指纹（小写）
     */
    static String fingerHex() {
        final String hex = fingerString(); // may throw RuntimeException
        // 只保留十六进制字符
        String cleaned = hex == null ? "" : hex.replaceAll("[^0-9A-Fa-f]", "");
        if (cleaned.isEmpty()) {
            return "";
        }
        // 若长度为奇数，前面补 0
        if ((cleaned.length() & 1) == 1) {
            cleaned = "0" + cleaned;
        }
        cleaned = cleaned.toLowerCase();

        // 分成两字符一组并用冒号连接
        final StringBuilder out = new StringBuilder();
        for (int i = 0; i < cleaned.length(); i += 2) {
            if (!out.isEmpty()) {
                out.append(':');
            }
            out.append(cleaned, i, i + 2);
        }
        return out.toString();
    }

    /**
     * 生成当前机器的硬件指纹
     *
     * @return 指纹字符串（十六进制形式）
     */
    static String fingerString() {
        try {
            final StringBuilder sb = new StringBuilder();

            // 🖥️ 操作系统信息
            sb.append(System.getProperty("os.name")).append("-");
            sb.append(System.getProperty("os.arch")).append("-");
            sb.append(System.getProperty("os.version")).append("-");

            // 🌐 主机名
            try {
                final InetAddress localHost = InetAddress.getLocalHost();
                sb.append(localHost.getHostName()).append("-");
            } catch (final Exception ignore) {
            }

            // 🪪 MAC 地址（取第一个非回环、可用网卡）
            try {
                final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                while (networkInterfaces.hasMoreElements()) {
                    final NetworkInterface ni = networkInterfaces.nextElement();
                    if (!ni.isLoopback() && ni.getHardwareAddress() != null) {
                        final byte[] mac = ni.getHardwareAddress();
                        for (final byte b : mac) {
                            sb.append(String.format("%02X", b));
                        }
                        sb.append("-");
                        break;
                    }
                }
            } catch (final Exception ignore) {
            }

            // ☕ JVM 信息
            sb.append(System.getProperty("java.vendor")).append("-");
            sb.append(System.getProperty("java.version"));

            // 🔒 计算 SHA-256 哈希，保证唯一 & 固定长度
            return EDHasher.encrypt(sb.toString(), AlgHash.SHA256);
        } catch (final Exception e) {
            throw new RuntimeException("[ R2MO ] 无法生成机器指纹", e);
        }
    }
}

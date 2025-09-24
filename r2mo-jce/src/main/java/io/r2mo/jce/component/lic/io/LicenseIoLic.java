package io.r2mo.jce.component.lic.io;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.io.HStore;
import io.r2mo.base.util.R2MO;
import io.r2mo.jce.common.HED;
import io.r2mo.jce.component.lic.domain.LicenseConfiguration;
import io.r2mo.jce.component.lic.domain.LicenseData;
import io.r2mo.jce.component.lic.domain.LicenseFile;
import io.r2mo.jce.component.lic.domain.LicensePath;
import io.r2mo.jce.constant.LicFormat;
import io.r2mo.typed.common.Binary;
import io.r2mo.typed.exception.web._401UnauthorizedException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.security.PublicKey;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2025-09-20
 */
@Slf4j
class LicenseIoLic extends AbstractLicenseIo implements LicenseIo {
    private final LicenseFormat formatter = new LicenseFormatLic();

    LicenseIoLic(final HStore store) {
        super(store);
    }

    @Override
    public Binary writeZip(final LicenseFile licenseFile, final LicenseConfiguration configuration) {
        final Set<String> files = this.writePath(licenseFile, configuration);
        /*
         * Step 4 | 打包输出
         * - 将 .lic / .sig / (可选 .key) 打包为 ZIP
         * - 返回 Binary，可直接落盘或传输
         */
        return this.store.inBinary(files);
    }

    @Override
    public Set<String> writePath(final LicenseFile licenseFile, final LicenseConfiguration configuration) {
        /*
         * Step 1 | 写入许可文件 (.lic)
         * - 将 LicenseFile 格式化为文本
         * - 存储在配置目录下，命名基于 LicenseId
         * ⚠️ 注意：.lic 文件必须始终存在
         */
        final String content = this.formatter.format(licenseFile);
        final String licPath = this.nameLic(licenseFile, configuration);
        this.store.write(licPath, content, false);
        log.info("[ R2MO ] 写入许可文件：{}", licPath);

        /*
         * Step 2 | 写入签名文件 (.sig)
         * - 存放数字签名，服务端私钥生成，客户端公钥验证
         * 🚨 异常点：如果签名丢失，将无法验证合法性
         */
        final byte[] signature = licenseFile.signature();
        final String sigPath = this.nameSig(licenseFile, configuration);
        this.store.write(sigPath, signature);
        log.info("[ R2MO ] 写入签名文件：{}", sigPath);

        final Set<String> files = new HashSet<>() {{
            this.add(licPath);
            this.add(sigPath);
        }};

        /*
         * Step 3 | 写入密钥文件 (.key) —— 可选
         * - 若 License 使用对称加密，则写入 SecretKey
         * - 使用配置算法加密保存
         * ⚠️ 注意：客户端解密 .lic 时必须依赖该文件
         */
        final SecretKey key = licenseFile.key();
        if (Objects.nonNull(key)) {
            final byte[] keyBytes = HED.encodeSecretKey(key, configuration.algEnc().value());
            if (Objects.nonNull(keyBytes) && keyBytes.length > 0) {
                final String keyPath = this.nameKey(licenseFile, configuration);
                this.store.write(keyPath, keyBytes);
                log.info("[ R2MO ] 写入密钥文件：{}", keyPath);
                files.add(keyPath);
            }
        }
        return files;
    }

    @Override
    public LicenseFile readIn(final String content, final LicFormat format, final LicenseConfiguration configuration) {
        /*
         * 【场景说明】
         * 1. 客户端上传 License 内容（通常是 .lic 文件）
         * 2. 跨服务传输：License 内容作为字符串参数传递（HTTP/MQ 等）
         * 3. 无本地文件存储：直接在内存中解析
         *
         * 【逻辑说明】
         * - 使用 formatter 解析 content
         * - 根据 configuration.encrypted() 判定写入 data 还是 encrypted 字段
         * - 最终返回完整的 LicenseFile
         */
        final boolean encrypted = configuration.encrypted();
        return this.formatter.parse(content, encrypted).format(format);
    }


    @Override
    public LicenseFile readIn(final LicensePath path, final LicenseConfiguration configuration) {
        /*
         * 【场景说明】
         * 1. 从服务端本地文件（HStore/磁盘）加载完整的 License
         * 2. LicensePath 为空：只加载签名等关键信息，不做内容解密
         * 3. 客户端上传时：若外层设置了 path.fileLicense，则会使用存储路径加载完整内容
         *
         * 【逻辑说明】
         * Step 1 | 加载许可文件 (.lic)
         *   - 如果 path 未设置 fileLicense，则仅返回空 LicenseFile
         *   - 否则读取 .lic 文件，并调用 readIn(content, format, config) 解析
         *   - ⚠️ 注意：缺失 .lic 文件，流程无法继续
         */
        final LicenseFile licenseFile;
        if (StrUtil.isEmpty(path.fileLicense())) {
            licenseFile = LicenseFile.builder().build();
        } else {
            final String licPath = this.nameLic(path, configuration);
            log.info("[ R2MO ] 读取许可文件：{}", licPath);
            final String content = this.store.inString(licPath);
            licenseFile = this.readIn(content, path.format(), configuration);
        }
        licenseFile.format(path.format()).id(path.id());

        /*
         * Step 2 | 加载签名文件 (.sig)
         *   - 必须存在，用于后续验签
         *   - 🚨 异常点：缺失或损坏，License 无法验证合法性
         */
        final String sigPath = this.nameSig(path, configuration);
        final byte[] signature = this.store.inBytes(sigPath);
        licenseFile.signature(signature);
        log.info("[ R2MO ] 读取签名文件：{}", sigPath);

        /*
         * Step 3 | 加载密钥文件 (.key) —— 仅加密模式
         *   - 如果 configuration.encrypted() = true，则尝试加载 key 文件
         *   - 将字节解码为 SecretKey，写入 LicenseFile
         *   - ⚠️ 若缺失，则无法解密 encrypted 部分
         */
        final boolean encrypted = configuration.encrypted();
        if (encrypted) {
            final String keyPath = this.nameKey(path, configuration);
            final byte[] keyBytes = this.store.inBytes(keyPath);
            if (Objects.nonNull(keyBytes) && keyBytes.length > 0) {
                final SecretKey key = HED.decodeSecretKey(keyBytes, configuration.algEnc().value());
                licenseFile.key(key);
                log.info("[ R2MO ] 读取密钥文件：{}", keyPath);
            }
        }

        /*
         * Step 4 | 返回完整的 LicenseFile
         */
        return licenseFile;
    }

    @Override
    public LicenseData verify(final LicenseFile licenseFile, final LicenseConfiguration configuration) {
        /*
         * Step 1 | 加载公钥
         * - 从存储中读取公钥，用于后续签名验证
         * 🚨 异常点：如果公钥文件不存在，无法继续校验
         */
        final String publicKeyPath = this.store.pHome(configuration.ioPublic());
        final PublicKey publicKey = this.store.inPublic(publicKeyPath);
        if (Objects.isNull(publicKey)) {
            throw new IllegalArgumentException("[ R2MO ] 公钥文件不存在，无法验证 License！");
        }

        /*
         * Step 2 | 提取待校验数据
         * - 判断 License 是否加密
         * - 如果加密：需要加载 SecretKey 解密
         * - 如果未加密：直接取 data 字段
         * ⚠️ 注意：licenseFile.encrypted / licenseFile.data 应该来自客户端传输
         *          外层调用必须替换，避免使用旧值
         */
        final byte[] stored;
        if (configuration.encrypted()) {
            final SecretKey secretKey = licenseFile.key();
            if (Objects.isNull(secretKey)) {
                throw new IllegalArgumentException("[ R2MO ] License 使用加密，但密钥文件缺失，无法解密！");
            }
            stored = HED.decrypt(licenseFile.encrypted(), secretKey);
        } else {
            stored = licenseFile.data();
        }

        /*
         * Step 3 | 验证签名
         * - 使用公钥、算法配置进行签名验证
         * 🚨 异常点：如果签名不匹配，说明 License 文件可能被篡改
         */
        final byte[] signature = licenseFile.signature();
        final boolean verified = HED.verify(stored, signature, publicKey, configuration.algSign().value());
        if (!verified) {
            throw new _401UnauthorizedException("[ R2MO ] License 签名验证失败，文件可能被篡改！");
        }

        /*
         * Step 4 | 反序列化数据
         * - 将解密/原始数据反序列化为 LicenseData
         * - 返回业务层进行授权判断
         */
        log.info("[ R2MO ] License 验证通过，返回业务数据！");
        return R2MO.deserialize(stored);
    }
}
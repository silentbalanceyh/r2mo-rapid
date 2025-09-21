package io.r2mo.jce.component.lic.io;

import io.r2mo.base.io.HStore;
import io.r2mo.jce.component.lic.LicenseService;
import io.r2mo.jce.component.lic.domain.LicenseConfiguration;
import io.r2mo.jce.component.lic.domain.LicenseData;
import io.r2mo.jce.component.lic.domain.LicenseFile;
import io.r2mo.jce.component.lic.domain.LicensePath;
import io.r2mo.jce.constant.LicFormat;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Binary;
import io.r2mo.typed.exception.AbstractException;

import java.util.Objects;

/**
 * License IO 接口
 * 📌 职责：
 * <pre>
 * 1. 🔑 写入 License（签发端）
 *    - 使用私钥对 License 明文签名
 *    - （可选）对内容进行加密，保护敏感信息
 *    - 将结果存储到文件系统（*.dat / *.lic / *.sig）
 *    - 打包成 zip 分发给客户
 *
 * 2. 📦 读取 License（内部调用）
 *    - 根据路径信息加载已生成的 lic/sig/dat 文件
 *    - 封装成 LicenseFile 供上层使用
 *
 * 3. ✅ 校验 License（验证端）
 *    - 校验上传的 License 文件与服务端存储一致性（Checksum）
 *    - 使用公钥验证签名是否有效
 *    - （可选）执行解密，还原 LicenseData
 *    - 返回给业务层用于授权逻辑判断
 * </pre>
 *
 * ⚠️ 注意：
 * - 任何校验失败必须抛出 {@link AbstractException}
 * - 业务层必须保证 Checksum 与 LicenseId 绑定，防止篡改
 *
 * @author lang
 * @since 2025-09-20
 */
public interface LicenseIo {
    Cc<String, LicenseIo> CC_IO = Cc.openThread();

    static LicenseIo of(final HStore store) {
        Objects.requireNonNull(store, "[ R2MO ] HStore 不能为空，当前服务必须对接存储！");
        return CC_IO.pick(() -> new LicenseIoCommon(store), String.valueOf(store.hashCode()));
    }

    /**
     * ✍️ 写入 License 文件
     * 流程：
     * <pre>
     * 1. 📂 路径计算
     *    - 根据 {@link LicenseConfiguration} 得到存储路径
     *    - 私钥路径：{@link LicenseConfiguration#ioPrivate()}
     *
     * 2. 📝 构造 LicenseFile
     *    - 调用 {@link LicenseService} 生成 LicenseFile 对象
     *    - 包含原始数据、加密数据、签名
     *
     * 3. 📄 数据提取
     *    - data() -> *.dat
     *    - encrypted() -> *.lic（含 LicenseId）
     *    - signature() -> *.sig
     *
     * 4. 💾 文件存储
     *    - 写入到 {@link LicenseConfiguration#contextLicense()}
     *
     * 5. 📦 打包分发
     *    - 将 *.dat / *.lic / *.sig 打包成 *.zip
     *    - 转换为 {@link Binary} 数据流返回
     * </pre>
     *
     * @param licenseFile   License 文件对象
     * @param configuration 配置对象（路径、算法等）
     *
     * @return 压缩包 zip 的数据流
     */
    Binary writeTo(LicenseFile licenseFile, LicenseConfiguration configuration);

    /**
     * 📥 读取 License 文件（服务端文件读取场景）
     * 流程：
     * <pre>
     * 1. 📂 路径计算
     *    - 根据 {@link LicenseConfiguration} 确定 License 目录
     *
     * 2. 📄 文件加载
     *    - *.lic -> LicenseFile.encrypted()
     *    - *.sig -> LicenseFile.signature()
     *    - *.dat -> LicenseFile.data()
     *
     * 3. 📦 封装结果
     *    - 将文件内容封装为 {@link LicenseFile}
     * </pre>
     *
     * 应用场景：
     * - 服务端本地校验：从磁盘或 HStore 加载文件
     * - 管理后台：展示或导出 License 内容
     *
     * @param path          License 路径定义（含 LicenseId 等）
     * @param configuration 配置对象（路径、算法等）
     *
     * @return 封装好的 {@link LicenseFile}
     */
    LicenseFile readIn(LicensePath path, LicenseConfiguration configuration);

    /**
     * 📥 读取 License 文件（客户端传输数据场景）
     * 流程：
     * <pre>
     * 1. 📄 内容解析
     *    - 根据 {@link LicFormat} 解析字符串内容
     *    - 解析 HEAD 部分（LicenseId、Name、Code）
     *    - 解析 BODY 部分（Base64 数据 -> data/encrypted）
     *
     * 2. 📦 封装结果
     *    - 将解析结果构造为 {@link LicenseFile}
     * </pre>
     *
     * 应用场景：
     * - 客户端上传：通过 API 上传 *.lic 内容
     * - 跨服务传输：通过 MQ/HTTP 携带 License 数据
     * - 无本地文件系统场景：直接在内存中解析内容
     *
     * ⚠️ 注意：
     * - 此方法的数据源来自外部客户端传输，必须在业务层保证 Checksum 与 LicenseId 一致性
     * - 若解析失败或数据损坏，应抛出 {@link AbstractException}
     *
     * @param content       License 文件的原始字符串内容
     * @param format        解析格式（{@link LicFormat}）
     * @param configuration 配置对象（路径、算法等）
     *
     * @return 封装好的 {@link LicenseFile}
     */
    LicenseFile readIn(String content, LicFormat format, LicenseConfiguration configuration);

    /**
     * 🔍 校验 License 文件
     * 流程：
     * <pre>
     * 1. 🔑 Checksum 校验
     *    - 上传的 *.lic 必须与 LicenseId 对应
     *    - 防止文件被篡改（业务层保证）
     *
     * 2. 📂 路径解析
     *    - 根据 {@link LicenseConfiguration} 定位 sig、公钥
     *
     * 3. ✅ 签名校验
     *    - 使用公钥 {@link LicenseConfiguration#ioPublic()} 验证签名
     *
     * 4. 🔓 内容解密（如果加密）
     *    - 使用对称密钥解密 lic 内容
     *
     * 5. 📦 转换数据
     *    - 构造 {@link LicenseData} 返回业务层使用
     * </pre>
     *
     * @param licenseFile   待验证的 License 文件
     * @param configuration 配置对象（路径、算法等）
     *
     * @return 成功时返回 {@link LicenseData}；失败抛出 {@link AbstractException}
     */
    LicenseData verify(LicenseFile licenseFile, LicenseConfiguration configuration);
}

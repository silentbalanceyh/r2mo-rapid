package io.r2mo.jce.component.lic.io;

import io.r2mo.base.io.HStore;
import io.r2mo.jce.component.lic.LicenseService;
import io.r2mo.jce.component.lic.domain.LicenseConfiguration;
import io.r2mo.jce.component.lic.domain.LicenseData;
import io.r2mo.jce.component.lic.domain.LicenseFile;
import io.r2mo.jce.component.lic.domain.LicensePath;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Binary;
import io.r2mo.typed.exception.AbstractException;

import java.io.InputStream;
import java.util.Objects;

/**
 * 根据路径地址读取 License 信息和写入 License 信息的专用接口
 * <pre>
 *     1. 写入 License / {@link LicenseFile}
 *     2. 读取 {@link LicenseFile}
 * </pre>
 *
 * @author lang : 2025-09-20
 */
public interface LicenseIo {
    Cc<String, LicenseIo> CC_IO = Cc.openThread();

    static LicenseIo of(final HStore store) {
        Objects.requireNonNull(store, "[ R2MO ] HStore 不能为空，当前服务必须对接存储！");
        return CC_IO.pick(() -> new LicenseIoCommon(store), String.valueOf(store.hashCode()));
    }

    /**
     * 将 License 文件写入到指定位置，执行流程：
     * <pre>
     *     流程：
     *     1. 根据实际内容计算 {@link LicenseConfiguration} 相关位置信息
     *        - 私钥位置 {@link LicenseConfiguration#ioPrivate()}
     *     2. 调用 {@link LicenseService} 执行构造操作，生成 LicenseFile 对象
     *     3. 将 LicenseFile 中的数据提取
     *        - {@link LicenseFile#data()} -> *.dat
     *        - {@link LicenseFile#encrypted()} -> *.lic ( 内容中要带上 LicenseId )
     *        - {@link LicenseFile#signature()} -> *.sig
     *     4. 将数据写入到指定位置 {@link LicenseConfiguration#ioLicenseDirectory()}，文件名使用
     *     5. 在指定位置打包 *.zip 文件，然后将此文件 *.zip 转换成 {@link InputStream}
     * </pre>
     *
     * @param licenseFile   License 文件对象
     * @param configuration 路径对象
     *
     * @return 返回 zip 压缩之后的 InputStream 数据流
     */
    Binary writeTo(LicenseFile licenseFile, LicenseConfiguration configuration);


    /**
     * （内部调用）从指定位置读取 LicenseFile 文件，执行流程：
     * <pre>
     *     流程：
     *     1. 根据实际内容计算 {@link LicenseConfiguration} 相关位置信息
     *     2. 根据路径信息加载 {@link LicenseFile} 文件对象
     *        - *.lic -> {@link LicenseFile#encrypted()} / 要解析 LicenseId
     *        - *.sig -> {@link LicenseFile#signature()}
     *        - *.dat -> {@link LicenseFile#data()}
     *     3. 返回 LicenseFile 对象
     * </pre>
     *
     * @param configuration 路径对象
     * @param path          License 文件格式
     *
     * @return 返回 License 文件对象
     */
    LicenseFile readIn(LicensePath path, LicenseConfiguration configuration);


    /**
     * <pre>
     *     流程：
     *     1. 用户上传 *.lic 文件进行初次校验，Checksum 必须一致
     *     2. 根据 LicenseId 计算 {@link LicenseConfiguration} 相关位置信息
     *        - 获取服务端存储的 *.sig 文件
     *        - 公钥位置 {@link LicenseConfiguration#ioPublic()}
     *     3. 校验当前签名是否合法，如果合法则直接转换成 {@link LicenseData} 给上层服务对象使用
     * </pre>
     * 注：此处的 Checksum 校验是为了防止用户上传的文件被篡改，必须和 LicenseId 相关联，而且不在当前服务内校验，而是在业务层校验，
     * Checksum 在业务层的 License 实体中进行存储，License 实体在应用的业务逻辑上处理，至于异常，所有位置都可以直接抛出
     * {@link AbstractException} 的自定义异常。
     *
     * @param configuration 路径对象
     * @param licenseFile   License 文件对象
     *
     * @return 正常返回 {@link LicenseData} / 异常抛出 {@link AbstractException}
     */
    LicenseData verify(LicenseFile licenseFile, LicenseConfiguration configuration);
}

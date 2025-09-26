package io.r2mo.spi;

import io.r2mo.base.io.HStore;
import io.r2mo.base.web.ForAbort;
import io.r2mo.base.web.ForLocale;
import io.r2mo.base.web.ForStatus;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * 🧩 SPI 接口连接点
 *
 * <p>
 * 本接口是系统中各类工厂与工具的统一访问入口，开发者通过此接口即可快速获取
 * 常用的 JSON 工具、IO 工具、Web 工具、数据库操作工厂等。
 * </p>
 *
 * <p>
 * 功能概览：
 * <ul>
 *   <li>⚙️ 提供 SPI 工厂（Object / DBAction / Io / Web）的全局单例访问</li>
 *   <li>📊 提供接口类型与实现类的映射信息</li>
 *   <li>🧰 提供常用工具对象（Json、IO 存储、Web 状态/多语言/异常处理）</li>
 *   <li>⚡ 提供 JSON 对象 / 数组的快速构造方法</li>
 *   <li>🔍 封装 SPI 查找逻辑（findOne / findMany），支持多 ClassLoader</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>
 *   JObject obj = SPI.J().set("key", "value");  // 快速构造 JSON 对象
 *   List&lt;MySPI&gt; services = SPI.findMany(MySPI.class); // 获取某接口的所有实现
 *   MySPI service = SPI.findOne(MySPI.class, "implName"); // 按名称获取单个实现
 * </pre>
 * </p>
 *
 * @author lang
 * @since 2025-08-28
 */
public interface SPI {

    // ======================
    // 🏭 SPI 工厂单例
    // ======================

    /** 🎭 对象工厂（Json 工具、对象处理） */
    FactoryObject SPI_OBJECT = ProviderOfFactory.forObject();

    /** 🗄️ 数据库操作工厂 */
    FactoryDBAction SPI_DB = ProviderOfFactory.forDBAction();

    /** 💾 IO 工厂 */
    FactoryIo SPI_IO = ProviderOfFactory.forIo();

    /** 🌐 Web 工厂 */
    FactoryWeb SPI_WEB = ProviderOfFactory.forWeb();

    /** 📊 SPI 接口类型与实现类映射表（已加载的实现类缓存） */
    ConcurrentMap<Class<?>, Class<?>> SPI_META = ProviderOfFactory.meta();


    // ======================
    // 🧰 专用组件工具
    // ======================

    /** 📑 JSON 工具（序列化、反序列化、格式化） */
    JUtil V_UTIL = SPI_OBJECT.jsonUtil();

    /** 🗂️ IO 存储工具 */
    HStore V_STORE = SPI_IO.ioAction();

    /** 📡 Web 状态码处理器 */
    ForStatus V_STATUS = SPI_WEB.ofStatus();

    /** 🌍 Web 国际化/多语言处理器 */
    ForLocale V_LOCALE = SPI_WEB.ofLocale();

    /** ⛔ Web 异常/中断处理器 */
    ForAbort V_ABORT = SPI_WEB.ofAbort();


    // ======================
    // ⚡ JSON 快速构造
    // ======================

    /** ✨ 快速构造一个空 JSON 对象 */
    static JObject J() {
        return SPI_OBJECT.jsonObject();
    }

    /** ✨ 快速构造一个空 JSON 数组 */
    static JArray A() {
        return SPI_OBJECT.jsonArray();
    }


    // ======================
    // 🔍 SPI 组件查找工具
    // ======================

    /**
     * 获取某接口的所有 SPI 实现（默认使用 TCCL）
     *
     * @param clazz 接口类型
     *
     * @return 所有实现类实例列表
     */
    static <T> List<T> findMany(final Class<T> clazz) {
        return ProviderOfFactory.findMany(clazz);
    }

    /**
     * 获取某接口的所有 SPI 实现（指定 ClassLoader）
     *
     * @param clazz  接口类型
     * @param loader 自定义类加载器
     *
     * @return 所有实现类实例列表
     */
    static <T> List<T> findMany(final Class<T> clazz, final ClassLoader loader) {
        return ProviderOfFactory.findMany(clazz, loader);
    }

    /**
     * 获取某接口的所有 SPI 实现（基于调用类的 ClassLoader）
     *
     * @param clazz  接口类型
     * @param caller 调用方类，用于获取其 ClassLoader
     *
     * @return 所有实现类实例列表
     */
    static <T> List<T> findMany(final Class<T> clazz, final Class<?> caller) {
        final ClassLoader loader = caller.getClassLoader();
        return ProviderOfFactory.findMany(clazz, loader);
    }

    /**
     * 获取单个 SPI 实现类（未指定名称）
     * - 若只有一个实现类则返回该实例；
     * - 若有多个实现类则返回 null；
     * - 若没有实现类则返回 null。
     */
    static <T> T findOne(final Class<T> clazz) {
        return ProviderOfFactory.findOne(clazz, null);
    }

    /**
     * 获取单个 SPI 实现类（按名称）
     *
     * @param clazz 接口类型
     * @param name  实现类名称（取自 @SPID 注解）
     *
     * @return 匹配的实现类实例，未找到则返回 null
     */
    static <T> T findOne(final Class<T> clazz, final String name) {
        return ProviderOfFactory.findOne(clazz, name);
    }
}

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
 *   List<MySPI> services = SPI.findMany(MySPI.class); // 获取某接口的所有实现
 *   MySPI service = SPI.findOne(MySPI.class, "implName"); // 按名称获取单个实现
 * </pre>
 * </p>
 *
 * @author lang
 * @since 2025-08-28
 */
public class SPI {

    // ======================
    // 🏭 SPI 工厂单例
    // ======================

    /** 🗄️ 数据库操作工厂 */
    public static final FactoryDBAction SPI_DB = ProviderOfFactory.forDBAction();
    /** 💾 IO 工厂 */
    public static final FactoryIo SPI_IO = ProviderOfFactory.forIo();
    /** 🌐 Web 工厂 */
    public static final FactoryWeb SPI_WEB = ProviderOfFactory.forWeb();
    /** 📊 SPI 接口类型与实现类映射表（已加载的实现类缓存） */
    public static final ConcurrentMap<Class<?>, Class<?>> SPI_META = ProviderOfFactory.meta();
    /** 🗂️ IO 存储工具 */
    public static final HStore V_STORE = SPI_IO.ioAction();


    // ======================
    // 🧰 专用组件工具
    // ======================
    /** 📡 Web 状态码处理器 */
    public static final ForStatus V_STATUS = SPI_WEB.ofStatus();
    /** 🌍 Web 国际化/多语言处理器 */
    public static final ForLocale V_LOCALE = SPI_WEB.ofLocale();
    /** ⛔ Web 异常/中断处理器 */
    public static final ForAbort V_ABORT = SPI_WEB.ofAbort();
    /** 🎭 对象工厂（Json 工具、对象处理） */
    private static final FactoryObject SPI_OBJECT = ProviderOfFactory.forObject();
    /** 📑 JSON 工具（序列化、反序列化、格式化） */
    public static final JUtil V_UTIL = SPI_OBJECT.jsonUtil();


    // ======================
    // ⚡ JSON 快速构造
    // ======================

    /** ✨ 快速构造一个空 JSON 对象 */
    public static JObject J() {
        return SPI_OBJECT.jsonObject();
    }

    public static JObject J(final String json) {
        return SPI_OBJECT.jsonObject(json);
    }

    public static JObject J(final Object value) {
        return SPI_OBJECT.jsonObject(value);
    }

    /** ✨ 快速构造一个空 JSON 数组 */
    public static JArray A() {
        return SPI_OBJECT.jsonArray();
    }

    public static JArray A(final String json) {
        return SPI_OBJECT.jsonArray(json);
    }

    public static JArray A(final Object value) {
        return SPI_OBJECT.jsonArray(value);
    }


    // ======================
    // 🔍 SPI 基础查找工具
    // ======================

    /**
     * 获取某接口的所有 SPI 实现（默认使用 TCCL）
     *
     * @param clazz 接口类型
     *
     * @return 所有实现类实例列表
     */
    public static <T> List<T> findMany(final Class<T> clazz) {
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
    public static <T> List<T> findMany(final Class<T> clazz, final ClassLoader loader) {
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
    public static <T> List<T> findMany(final Class<T> clazz, final Class<?> caller) {
        final ClassLoader loader = caller.getClassLoader();
        return ProviderOfFactory.findMany(clazz, loader);
    }

    /**
     * 获取单个 SPI 实现类（未指定名称）
     * - 若只有一个实现类则返回该实例；
     * - 若有多个实现类则返回 null；
     * - 若没有实现类则返回 null。
     */
    public static <T> T findOne(final Class<T> clazz) {
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
    public static <T> T findOne(final Class<T> clazz, final String name) {
        return ProviderOfFactory.findOne(clazz, name);
    }


    // ======================
    // 🚀 SPI 高级查找工具
    // ======================

    /**
     * 🏆 获取优先级最高的单个 SPI 实现类
     * <pre>
     *     🎯 功能：
     *     - 根据 @SPID 注解中的 priority 值查找最高优先级的实现
     *     - 适用于需要按优先级选择实现的场景
     *
     *     🏷️ 优先级规则：
     *     - Zero 核心组件：priority = 0
     *     - Zero Extension 扩展组件：priority = 1017
     *     - Zero 自定义组件：priority > 2000
     * </pre>
     *
     * @param clazz 接口类型
     * @param <T>   接口泛型
     *
     * @return 优先级最高的实现类实例，未找到则返回 null
     */
    public static <T> T findOneOf(final Class<T> clazz) {
        return ProviderOfFactory.findOneOf(clazz);
    }

    /**
     * 🔄 获取可覆盖的单个 SPI 实现类
     * <pre>
     *     🎯 功能：
     *     - 从多个实现中选择一个用于覆盖默认行为
     *     - 通常用于扩展和自定义实现的场景
     *
     *     🏗️ 使用场景：
     *     - 自定义扩展覆盖默认实现
     *     - 优先级高的实现覆盖优先级低的实现
     * </pre>
     *
     * @param clazz 接口类型
     * @param <T>   接口泛型
     *
     * @return 可覆盖的实现类实例，未找到则返回 null
     */
    public static <T> T findOverwrite(final Class<T> clazz) {
        final List<T> found = SPI.findMany(clazz);
        return ProviderOfFactory.findOverwrite(found, clazz);
    }

    /**
     * 🔄 获取可覆盖的单个 SPI 实现类（指定 ClassLoader）
     * <pre>
     *     🎯 功能：
     *     - 从指定 ClassLoader 的多个实现中选择一个用于覆盖默认行为
     *     - 支持多 ClassLoader 环境下的覆盖实现查找
     * </pre>
     *
     * @param clazz       接口类型
     * @param classLoader 用于加载实现类的 ClassLoader
     * @param <T>         接口泛型
     *
     * @return 可覆盖的实现类实例，未找到则返回 null
     */
    public static <T> T findOverwrite(final Class<T> clazz, final Class<?> classLoader) {
        final List<T> found = SPI.findMany(clazz, classLoader);
        return ProviderOfFactory.findOverwrite(found, clazz);
    }
}
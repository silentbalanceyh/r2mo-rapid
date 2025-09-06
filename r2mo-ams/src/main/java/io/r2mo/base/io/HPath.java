package io.r2mo.base.io;

import java.nio.file.Path;

/**
 * 根目录查询，主要用于提取根目录信息，其中根目录路径包括如下
 * <pre>
 *     1. home 目录，通常是 XXX_HOME 这种产品级的 HOME 运行目录，可以从环境变量中提取
 *     2. user 目录，通常是用户的工作主目录
 *     3. java 目录，通常是 JDK 的安装目录，如果设置了 JAVA_HOME 则直接提取
 * </pre>
 *
 * @author lang : 2025-09-06
 */
public interface HPath {

    String PATH_DEFAULT_NAME = "PATH_DEFAULT_NAME";

    Path pathHome(String path, String envName);

    Path pathUser(String path);

    Path pathJava(String path);
}

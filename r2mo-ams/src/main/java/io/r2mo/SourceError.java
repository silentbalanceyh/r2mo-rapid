package io.r2mo;

import io.r2mo.base.web.i18n.ForLocaleReport;
import io.r2mo.typed.exception.AbstractException;
import org.reflections.Reflections;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lang : 2025-11-13
 */
public class SourceError {

    // 匹配类名：以 _ + 5或6位数字 开头
    private static final Pattern PREFIX_CODE_PATTERN = Pattern.compile("^_([0-9]{5,6})(.*)$");
    // 在剩余部分中找第一个 3 位数字（HTTP状态码）
    private static final Pattern HTTP_STATUS_PATTERN = Pattern.compile("([0-9]{3})");

    // 类名最小宽度（与 printList 一致）
    private static final int CLASS_NAME_WIDTH = 64;

    public static void printList(final String... scanPackages) {
        printList(AbstractException.class, scanPackages);
    }

    public static void printExist(final int code, final String... scanPackages) {
        printExist(AbstractException.class, code, scanPackages);
    }

    @SuppressWarnings("all")
    public static void printPath(final Class<?> errorCls, final String field) {
        try {
            // 检查 JVM 到底加载了哪个 JAR 包里的 ERR 类
            final Class<?> errClass = Class.forName(errorCls.getName());
            final java.security.CodeSource source = errClass.getProtectionDomain().getCodeSource();
            System.err.println(">>>>>> [DEBUG] ERR 类加载自: " + source.getLocation());

            // 检查能不能反射读到字段
            System.err.println(">>>>>> [DEBUG] 尝试获取字段: " + errClass.getField(field));
        } catch (final Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查指定错误码（如 -20000 或 20000）是否已定义，并打印其完整行信息（若存在）
     *
     * @param rootClass    异常根类（用于扫描继承树）
     * @param code         错误码编号（支持负数或正数，内部取绝对值）
     * @param scanPackages 要扫描的包（可变参数）
     */
    public static void printExist(final Class<?> rootClass, final int code, final String... scanPackages) {
        if (rootClass == null) {
            System.out.println("[ R2MO ] 根类不能为 null。");
            return;
        }

        final String codeStr = String.format("%05d", Math.abs(code)); // 如 20000
        final String expectedPrefix = "_" + codeStr;

        final Map<String, String> errorMessages = ForLocaleReport.getMapError();
        final Map<String, String> infoMessages = ForLocaleReport.getMapInfo();

        final Set<Class<?>> allSubTypes = scan(rootClass, determinePackagesToScan(rootClass, scanPackages));

        final Set<Class<?>> allClasses = new HashSet<>(allSubTypes);
        allClasses.add(rootClass);

        Class<?> matchedClass = null;
        for (final Class<?> cls : allClasses) {
            if (cls.getSimpleName().startsWith(expectedPrefix)) {
                matchedClass = cls;
                break;
            }
        }

        if (matchedClass == null) {
            System.out.println("[ R2MO ] 未找到类名以 \"" + expectedPrefix + "\" 开头的异常类。");
            return;
        }

        // 提取 HTTP 状态码
        final String simpleName = matchedClass.getSimpleName();
        final String remainder = simpleName.substring(expectedPrefix.length());
        final Matcher statusMatcher = HTTP_STATUS_PATTERN.matcher(remainder);
        final String httpStatus = statusMatcher.find() ? statusMatcher.group(1) : "???";

        // 获取消息
        final String eKey = "E" + codeStr;
        final String iKey = "I" + codeStr;
        final String errorMsg = errorMessages.get(eKey);
        final String infoMsg = infoMessages.get(iKey);
        final String errorDisplay = (errorMsg != null) ? errorMsg : "( N/A )";
        final String infoDisplay = (infoMsg != null) ? infoMsg : "( N/A )";

        // 统一格式：与 printList 完全一致
        final String formattedClassName = String.format("%-" + CLASS_NAME_WIDTH + "s", simpleName);
        final String entry = "-" + codeStr
            + " - ( " + httpStatus + " ) "
            + formattedClassName
            + " - ⭕️ " + errorDisplay
            + " \t\t/ 〽️ " + infoDisplay;

        System.out.println("[ R2MO ] 匹配到定义：");
        System.out.println(entry);
    }

    /**
     * 打印符合 _NNNNN 前缀规则的异常清单，并关联 ERROR/INFO 国际化消息
     */
    public static void printList(final Class<?> rootClass, final String... scanPackages) {
        if (rootClass == null) {
            System.out.println("[ R2MO ] 根类不能为 null。");
            return;
        }

        final Set<Class<?>> allSubTypes = scan(rootClass, determinePackagesToScan(rootClass, scanPackages));

        final Set<Class<?>> allClasses = new HashSet<>(allSubTypes);
        allClasses.add(rootClass);

        final Map<String, String> errorMessages = ForLocaleReport.getMapError();
        final Map<String, String> infoMessages = ForLocaleReport.getMapInfo();

        final List<String> entries = new ArrayList<>();

        for (final Class<?> cls : allClasses) {
            final String simpleName = cls.getSimpleName();
            final Matcher prefixMatcher = PREFIX_CODE_PATTERN.matcher(simpleName);
            if (prefixMatcher.matches()) {
                final String errorCodeStr = prefixMatcher.group(1);
                final String remainder = prefixMatcher.group(2);

                final Matcher statusMatcher = HTTP_STATUS_PATTERN.matcher(remainder);
                final String httpStatus = statusMatcher.find() ? statusMatcher.group(1) : "???";

                final String eKey = "E" + errorCodeStr;
                final String iKey = "I" + errorCodeStr;

                final String errorMsg = errorMessages.get(eKey);
                final String infoMsg = infoMessages.get(iKey);

                final String errorDisplay = (errorMsg != null) ? errorMsg : "( N/A )";
                final String infoDisplay = (infoMsg != null) ? infoMsg : "( N/A )";

                final String formattedClassName = String.format("%-" + CLASS_NAME_WIDTH + "s", simpleName);
                final String entry = "-" + errorCodeStr
                    + " - ( " + httpStatus + " ) "
                    + formattedClassName
                    + " - ⭕️ " + errorDisplay
                    + " \t\t/ 〽️ " + infoDisplay;

                entries.add(entry);
            }
        }

        if (!entries.isEmpty()) {
            entries.sort(Comparator.comparingLong(s -> {
                final int start = s.indexOf('-') + 1;
                final int end = s.indexOf(' ', start);
                return Long.parseLong(s.substring(start, end));
            }));
            System.out.println("[ R2MO ] 异常表：");
            for (final String entry : entries) {
                System.out.println(entry);
            }
        } else {
            System.out.println("[ R2MO ] 未找到符合 _NNNNN 前缀命名规则的异常类。");
        }
    }

    public static void printTree(final String... scanPackages) {
        printTree(AbstractException.class, scanPackages);
    }

    /**
     * 打印异常继承树
     */
    public static void printTree(final Class<?> rootClass, final String... scanPackages) {
        if (rootClass == null) {
            System.out.println("[ R2MO ] 根类不能为 null。");
            return;
        }

        final Set<Class<?>> allSubTypes = scan(rootClass, determinePackagesToScan(rootClass, scanPackages));

        final Set<Class<?>> allClasses = new HashSet<>(allSubTypes);
        allClasses.add(rootClass);

        final Map<Class<?>, List<Class<?>>> tree = buildInheritanceTree(allClasses);

        System.out.println("[ R2MO ] 异常继承树：" + rootClass.getName());
        printTree(rootClass, tree, "", true);
    }

    private static List<String> determinePackagesToScan(final Class<?> rootClass, final String... scanPackages) {
        final List<String> packagesToScan = new ArrayList<>();
        boolean hasValidPackage = false;

        if (scanPackages != null) {
            for (final String pkg : scanPackages) {
                if (pkg != null && !pkg.trim().isEmpty()) {
                    packagesToScan.add(pkg.trim());
                    hasValidPackage = true;
                }
            }
        }

        if (!hasValidPackage) {
            final String rootPkg = rootClass.getPackage() != null ? rootClass.getPackage().getName() : "";
            if (!rootPkg.isEmpty()) {
                packagesToScan.add(rootPkg);
            } else {
                packagesToScan.add("");
            }
        }

        return packagesToScan;
    }

    private static Map<Class<?>, List<Class<?>>> buildInheritanceTree(final Set<Class<?>> classes) {
        final Map<Class<?>, List<Class<?>>> parentToChildren = new HashMap<>();

        for (final Class<?> cls : classes) {
            parentToChildren.put(cls, new ArrayList<>());
        }

        for (final Class<?> cls : classes) {
            final Class<?> parent = cls.getSuperclass();
            if (parent != null && parentToChildren.containsKey(parent)) {
                parentToChildren.get(parent).add(cls);
            }
        }

        for (final List<Class<?>> children : parentToChildren.values()) {
            children.sort(Comparator.comparing(Class::getSimpleName));
        }

        return parentToChildren;
    }

    private static void printTree(final Class<?> current, final Map<Class<?>, List<Class<?>>> tree, final String prefix, final boolean isLast) {
        System.out.println(prefix + (isLast ? "└── " : "├── ") + current.getSimpleName());

        final List<Class<?>> children = tree.getOrDefault(current, Collections.emptyList());
        if (children.isEmpty()) {
            return;
        }

        for (int i = 0; i < children.size(); i++) {
            final Class<?> child = children.get(i);
            final boolean isLastChild = (i == children.size() - 1);
            final String newPrefix = prefix + (isLast ? "    " : "│   ");
            printTree(child, tree, newPrefix, isLastChild);
        }
    }

    @SuppressWarnings("unchecked")
    private static Set<Class<?>> scan(final Class<?> rootClass, final List<String> packagesToScan) {
        final Set<Class<?>> allSubTypes = new HashSet<>();
        final boolean scanAll = packagesToScan.stream()
            .anyMatch(pkg -> pkg == null || pkg.trim().isEmpty());

        if (scanAll) {
            allSubTypes.addAll(new Reflections().getSubTypesOf(rootClass));
        } else {
            if (!packagesToScan.isEmpty()) {
                final Reflections reflections = new Reflections(packagesToScan.toArray(new Object[0]));
                allSubTypes.addAll(reflections.getSubTypesOf(rootClass));
            }
        }
        return allSubTypes;
    }
}
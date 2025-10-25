package io.r2mo.dbe.jooq.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jooq.Table;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * [ R2MO ] jOOQ 表元数据 ↔ POJO 字段名 强韧映射工具（仅暴露 build）
 *
 * 目标：返回 ConcurrentMap<String,String>，键为 fieldName，值为 column_name。
 * 优先级顺序：
 * 0) 兼容旧版：字段数与列数一致时，先按索引一一对应（最快、最稳）
 * 1) 注解优先：@JsonProperty（不再使用 JPA 的 @Column）
 * 2) 确定性规则：同名（忽略大小写）、camel<->snake、去下划线/小写归一
 * 3) 布尔字段特例：isX/hasX/canX ↔ x / is_x
 * 4) 相似度兜底：Levenshtein 最小编辑距离（阈值 3）
 * 5) 最终兜底：将剩余字段按顺序分配给剩余列，保证“必有映射”
 */
public final class JooqMap {

    private JooqMap() {
    }

    /** [ R2MO ] 构建 fieldName -> column_name 的映射（强韧版） */
    public static ConcurrentMap<String, String> build(final Table<?> table, final Class<?> pojoClass) {
        Objects.requireNonNull(table, "[ R2MO ] 参数 table 不能为空");
        Objects.requireNonNull(pojoClass, "[ R2MO ] 参数 pojoClass 不能为空");

        // ✅ 使用 Field<?>[]，避免不同 jOOQ 版本/实现下的 TableField 转型问题
        final org.jooq.Field<?>[] colsArr = table.fields();
        final List<String> columns = toColumnNames(colsArr);

        final Field[] fieldsArr = declaredInstanceFields(pojoClass);
        final LinkedHashMap<String, Field> pojoFields = toFieldMap(fieldsArr);

        final ConcurrentMap<String, String> mapping = new ConcurrentHashMap<>();
        final Set<String> usedCols = new HashSet<>();

        /* [ R2MO ] 0) 旧版兼容：按索引一一对应（当数量一致时优先） */
        if (fieldsArr.length == colsArr.length && fieldsArr.length > 0) {
            for (int i = 0; i < colsArr.length; i++) {
                final String fn = fieldsArr[i].getName();
                final String cn = colsArr[i].getName();
                mapping.put(fn, cn);
                usedCols.add(cn);
            }
        }

        /* [ R2MO ] 1) 注解优先：@JsonProperty（不使用 JPA 的 @Column） */
        for (final Map.Entry<String, Field> e : pojoFields.entrySet()) {
            final String fn = e.getKey();
            final Field f = e.getValue();

            final JsonProperty jp = f.getDeclaredAnnotation(JsonProperty.class);
            if (jp != null && !jp.value().isEmpty()) {
                final String col = findColumnInsensitive(columns, stripDelimiters(jp.value()));
                if (col != null) {
                    mapping.put(fn, col);
                    usedCols.add(col);
                    // 若 @JsonProperty 的值与字段名不同，追加“别名字段 → 同列”的映射
                    if (!fn.equals(jp.value())) {
                        mapping.put(jp.value(), col);
                    }
                    continue;
                }
            }
        }

        /* [ R2MO ] 2) 确定性规则：同名 / camel->snake / 归一化匹配 */
        for (final String fn : pojoFields.keySet()) {
            if (mapping.containsKey(fn)) {
                continue;
            }

            // 2.1 同名（大小写不敏感）
            final String c1 = findColumnInsensitive(columns, fn);
            if (tryBind(mapping, usedCols, fn, c1)) {
                continue;
            }

            // 2.2 camel -> snake
            final String snake = camelToSnake(fn);
            final String c2 = findColumnInsensitive(columns, snake);
            if (tryBind(mapping, usedCols, fn, c2)) {
                continue;
            }

            // 2.3 去下划线+小写归一后比对
            final String normF = normalizeName(fn);
            final String c3 = columns.stream()
                .filter(c -> !usedCols.contains(c))
                .filter(c -> normalizeName(c).equals(normF))
                .findFirst().orElse(null);
            if (tryBind(mapping, usedCols, fn, c3)) {
                continue;
            }
        }

        /* [ R2MO ] 3) 布尔字段：isX / hasX / canX ↔ x / is_x */
        for (final String fn : pojoFields.keySet()) {
            if (mapping.containsKey(fn)) {
                continue;
            }
            if (isBooleanStyle(fn)) {
                final String core = booleanCore(fn);           // isActive -> active
                final String snake = camelToSnake(core);        // active -> active / is_active 变体
                final String c = firstNonNull(
                    findColumnInsensitive(columns, core),
                    findColumnInsensitive(columns, snake),
                    findColumnInsensitive(columns, "is_" + snake)
                );
                if (tryBind(mapping, usedCols, fn, c)) {
                    continue;
                }
            }
        }

        /* [ R2MO ] 4) 相似度兜底：Levenshtein（阈值 3） */
        for (final String fn : pojoFields.keySet()) {
            if (mapping.containsKey(fn)) {
                continue;
            }
            final String best = findBestByDistance(fn, columns, usedCols);
            if (tryBind(mapping, usedCols, fn, best)) {
                continue;
            }
        }

        /* [ R2MO ] 5) 最终兜底：把剩余字段依序绑定到剩余列，确保“必有映射” */
        final Iterator<String> restCols = columns.stream().filter(c -> !usedCols.contains(c)).iterator();
        for (final String fn : pojoFields.keySet()) {
            if (!mapping.containsKey(fn) && restCols.hasNext()) {
                final String col = restCols.next();
                mapping.put(fn, col);
                usedCols.add(col);
            }
        }

        return mapping;
    }

    /* ==================== 私有：基础工具 ==================== */

    private static List<String> toColumnNames(final org.jooq.Field<?>[] tableFields) {
        final List<String> cols = new ArrayList<>(tableFields.length);
        for (final org.jooq.Field<?> f : tableFields) {
            cols.add(f.getName());
        }
        return cols;
    }

    /** [ R2MO ] 仅收集实例字段（含父类），保持声明顺序 */
    private static Field[] declaredInstanceFields(final Class<?> pojoClass) {
        final List<Field> list = new ArrayList<>();
        for (Class<?> c = pojoClass; c != null && c != Object.class; c = c.getSuperclass()) {
            for (final Field f : c.getDeclaredFields()) {
                if (!java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
                    list.add(f);
                }
            }
        }
        return list.toArray(new Field[0]);
    }

    private static LinkedHashMap<String, Field> toFieldMap(final Field[] fields) {
        final LinkedHashMap<String, Field> map = new LinkedHashMap<>(fields.length * 2);
        for (final Field f : fields) {
            map.putIfAbsent(f.getName(), f);
        }
        return map;
    }

    private static boolean tryBind(final ConcurrentMap<String, String> mapping, final Set<String> used, final String field, final String column) {
        if (column == null) {
            return false;
        }
        mapping.put(field, column);
        used.add(column);
        return true;
    }

    private static String stripDelimiters(final String s) {
        if (s == null) {
            return null;
        }
        return s.replace("`", "").replace("\"", "").replace("[", "").replace("]", "");
    }

    private static String normalizeName(final String s) {
        if (s == null) {
            return "";
        }
        return s.replace("_", "").toLowerCase(Locale.ROOT);
    }

    private static String findColumnInsensitive(final List<String> columns, final String name) {
        if (name == null) {
            return null;
        }
        final String target = stripDelimiters(name);
        for (final String c : columns) {
            if (c.equals(target) || c.equalsIgnoreCase(target)) {
                return c;
            }
        }
        return null;
    }

    /* ==================== 私有：命名策略 ==================== */

    private static boolean isBooleanStyle(final String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            return false;
        }
        // 常见布尔前缀
        return fieldName.startsWith("is") || fieldName.startsWith("has") || fieldName.startsWith("can");
    }

    private static String booleanCore(final String fieldName) {
        if (fieldName.startsWith("is") && fieldName.length() > 2) {
            return uncapitalize(fieldName.substring(2));
        }
        if (fieldName.startsWith("has") && fieldName.length() > 3) {
            return uncapitalize(fieldName.substring(3));
        }
        if (fieldName.startsWith("can") && fieldName.length() > 3) {
            return uncapitalize(fieldName.substring(3));
        }
        return fieldName;
    }

    private static String uncapitalize(final String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        final char c0 = s.charAt(0);
        return Character.isUpperCase(c0) ? Character.toLowerCase(c0) + s.substring(1) : s;
    }

    private static String camelToSnake(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        s = stripCommonPrefixes(s);
        final StringBuilder sb = new StringBuilder(s.length() + 8);
        for (int i = 0; i < s.length(); i++) {
            final char ch = s.charAt(i);
            if (Character.isUpperCase(ch)) {
                if (i > 0) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(ch));
            } else {
                sb.append(ch);
            }
        }
        // 统一处理末尾 id 之类的常见缩写
        return sb.toString().replaceAll("id$", "id");
    }

    private static String stripCommonPrefixes(final String s) {
        if (s.startsWith("get") && s.length() > 3) {
            return uncapitalize(s.substring(3));
        }
        if (s.startsWith("set") && s.length() > 3) {
            return uncapitalize(s.substring(3));
        }
        if (s.startsWith("has") && s.length() > 3) {
            return uncapitalize(s.substring(3));
        }
        if (s.startsWith("is") && s.length() > 2) {
            return uncapitalize(s.substring(2));
        }
        if (s.startsWith("m_") && s.length() > 2) {
            return s.substring(2);
        }
        if (s.startsWith("_") && s.length() > 1) {
            return s.substring(1);
        }
        if (s.startsWith("$") && s.length() > 1) {
            return s.substring(1);
        }
        return s;
    }

    /* ==================== 私有：相似度 & 兜底 ==================== */

    private static String findBestByDistance(final String fieldName, final List<String> columns, final Set<String> used) {
        if (columns.isEmpty()) {
            return null;
        }
        final String normF = normalizeCandidate(fieldName);
        int best = Integer.MAX_VALUE;
        String bestCol = null;
        for (final String c : columns) {
            if (used.contains(c)) {
                continue;
            }
            final String normC = normalizeCandidate(c);
            final int d = levenshtein(normF, normC);
            if (d < best && d <= 3) {        // [ R2MO ] 阈值 3，可按需调整
                best = d;
                bestCol = c;
                if (d == 0) {
                    break;           // 形同命中直接结束
                }
            }
        }
        return bestCol;
    }

    private static String normalizeCandidate(final String s) {
        if (s == null) {
            return "";
        }
        String x = camelToSnake(s);
        x = x.replaceFirst("^is_", ""); // is_active -> active
        x = x.replace("_", "");
        return x.toLowerCase(Locale.ROOT);
    }

    // 经典 O(n*m) Levenshtein，短字符串足够快
    private static int levenshtein(final String a, final String b) {
        final int n = a.length();
        final int m = b.length();
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        int[] prev = new int[m + 1];
        int[] curr = new int[m + 1];
        for (int j = 0; j <= m; j++) {
            prev[j] = j;
        }
        for (int i = 1; i <= n; i++) {
            curr[0] = i;
            final char ca = a.charAt(i - 1);
            for (int j = 1; j <= m; j++) {
                final int cost = (ca == b.charAt(j - 1)) ? 0 : 1;
                curr[j] = Math.min(Math.min(curr[j - 1] + 1, prev[j] + 1), prev[j - 1] + cost);
            }
            final int[] tmp = prev;
            prev = curr;
            curr = tmp;
        }
        return prev[m];
    }

    private static String firstNonNull(final String... arr) {
        for (final String s : arr) {
            if (s != null) {
                return s;
            }
        }
        return null;
    }
}

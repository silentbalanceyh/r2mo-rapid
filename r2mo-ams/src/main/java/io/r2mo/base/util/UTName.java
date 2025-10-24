package io.r2mo.base.util;

import java.util.Set;

/**
 * @author lang : 2025-10-24
 */
class UTName {
    // ✅ 跨常见数据库（PG/MySQL/SQLServer/Oracle/SQLite）的“共同最小子集”校验
    static boolean isNamedSQL(final String raw) {
        return validateSqlIdentifierCommonReport(raw).ok;
    }

    /* --------- 下面是实现细节：报告 + 校验逻辑 --------- */

    private static final int COMMON_MAX_LEN = 30; // 取 Oracle 30 作为共同下限（更保守更可移植）

    private static final Set<String> RESERVED_COMMON = Set.of(
        // 一组保守的 SQL92/常见保留字（全大写比对）
        "SELECT", "INSERT", "UPDATE", "DELETE", "CREATE", "ALTER", "DROP", "TRUNCATE",
        "TABLE", "VIEW", "INDEX", "SEQUENCE", "FUNCTION", "PROCEDURE", "TRIGGER",
        "FROM", "WHERE", "GROUP", "BY", "HAVING", "ORDER", "LIMIT", "OFFSET",
        "JOIN", "LEFT", "RIGHT", "FULL", "INNER", "OUTER", "ON", "UNION", "DISTINCT",
        "AND", "OR", "NOT", "IN", "IS", "NULL", "LIKE", "BETWEEN", "CASE", "WHEN", "THEN", "ELSE", "END", "AS",
        // 类型/控制等
        "INT", "INTEGER", "SMALLINT", "BIGINT", "DECIMAL", "NUMERIC", "FLOAT", "REAL", "DOUBLE",
        "CHAR", "VARCHAR", "NCHAR", "NVARCHAR", "TEXT", "DATE", "TIME", "TIMESTAMP", "INTERVAL", "BOOLEAN",
        // 其他常见
        "PRIMARY", "KEY", "FOREIGN", "REFERENCES", "CHECK", "DEFAULT", "UNIQUE",
        "EXISTS", "ANY", "SOME", "ALL", "WITH", "OVER", "PARTITION", "RANGE", "ROWS",
        "TOP", "RETURNING", "MERGE", "MINUS", "EXCEPT", "INTERSECT"
    );

    /** 校验报告：ok=false 时，reason 给出第一处不通过原因 */
    private static final class SqlIdReport {
        final boolean ok;
        final String reason;

        SqlIdReport(final boolean ok, final String reason) {
            this.ok = ok;
            this.reason = reason;
        }

        @Override
        public String toString() {
            return (this.ok ? "OK" : "FAIL") + (this.reason == null ? "" : (": " + this.reason));
        }
    }

    /** 生成详细报告，便于调试（不想要报告可只用上面的 boolean 入口） */
    private static SqlIdReport validateSqlIdentifierCommonReport(final String raw) {
        if (raw == null || raw.isEmpty()) {
            return new SqlIdReport(false, "空字符串");
        }
        // 不接受任何引号或方括号/反引号（为保证跨库移植）
        if (isDelimited(raw)) {
            return new SqlIdReport(false, "跨库最小子集不接受加引号/反引号/方括号的标识符");
        }

        // schema.table(.column) 拆段
        final String[] parts = raw.split("\\.");
        if (parts.length == 0) {
            return new SqlIdReport(false, "拆分失败");
        }

        for (int idx = 0; idx < parts.length; idx++) {
            final String seg = parts[idx];
            final String where = "(第 " + (idx + 1) + " 段)";

            if (seg == null || seg.isEmpty()) {
                return new SqlIdReport(false, "存在空段 " + where);
            }
            if (seg.length() > COMMON_MAX_LEN) {
                return new SqlIdReport(false, "长度超过 " + COMMON_MAX_LEN + " 字符 " + where);
            }
            // 共同的未加引号规则：^[A-Za-z_][A-Za-z0-9_]*$
            if (!seg.matches("^[A-Za-z_][A-Za-z0-9_]*$")) {
                return new SqlIdReport(false, "仅允许字母/数字/下划线，且不能以数字开头 " + where);
            }
            // 禁 # 或 @ 等前缀（SQLServer/MySQL 临时/变量等方言特性，跨库不稳）
            if (seg.charAt(0) == '#' || seg.charAt(0) == '@' || seg.charAt(0) == '$') {
                return new SqlIdReport(false, "不允许使用临时/变量类前缀（#/@/$）" + where);
            }
            // 关键字（大小写不敏感）
            final String upper = seg.toUpperCase(java.util.Locale.ROOT);
            if (RESERVED_COMMON.contains(upper)) {
                return new SqlIdReport(false, "命中常见保留关键字：" + seg + " " + where);
            }
        }
        return new SqlIdReport(true, null);
    }

    /* --------- 辅助：是否被各种定界符包裹（为跨库最小子集一律拒绝） --------- */
    private static boolean isDelimited(final String s) {
        if (s.length() >= 2) {
            final char f = s.charAt(0);
            final char l = s.charAt(s.length() - 1);
            if ((f == '"' && l == '"')  // ANSI/PG/Oracle
                || (f == '`' && l == '`')  // MySQL
                || (f == '[' && l == ']')) // SQL Server
            {
                return true;
            }
        }
        // 也拒绝任何段内混用引号（例如 schema."T"）
        return s.indexOf('"') >= 0 || s.indexOf('`') >= 0 || s.indexOf('[') >= 0 || s.indexOf(']') >= 0;
    }


    // 私有：判断字符串是否是“可用作标识符的合法 Java 名称”
    static boolean isNamedJava(final String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }
        if (JAVA_RESERVED.contains(s)) {
            return false;
        }

        // 逐 code point 校验，支持完整 Unicode
        int i = 0;
        int cp = s.codePointAt(i);
        if (!Character.isJavaIdentifierStart(cp)) {
            return false;
        }
        i += Character.charCount(cp);

        while (i < s.length()) {
            cp = s.codePointAt(i);
            if (!Character.isJavaIdentifierPart(cp)) {
                return false;
            }
            i += Character.charCount(cp);
        }
        return true;
    }

    /* 关键字/保留字清单（面向 Java 16+，含 record 与单独下划线） */
    private static final java.util.Set<String> JAVA_RESERVED = java.util.Set.of(
        // 关键字
        "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
        "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
        "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
        "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp",
        "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void",
        "volatile", "while",
        // 新增/重要
        "record",  // Java 16 起为关键字
        "_",       // Java 9 起单独下划线是关键字（不可用作标识符）
        // 字面量（也不可作为标识符）
        "true", "false", "null"
    );
}

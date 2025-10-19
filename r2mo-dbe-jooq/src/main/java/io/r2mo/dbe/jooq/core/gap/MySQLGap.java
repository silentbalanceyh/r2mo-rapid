package io.r2mo.dbe.jooq.core.gap;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * @author lang : 2025-10-19
 */
public class MySQLGap {
    public static final Set<String> KEYWORD_SET = new HashSet<>() {
        {
            /*
             * SQL关键字
             * MySQL
             * - KEY, NAME, GROUP, TO
             */
            this.add("KEY");
            this.add("GROUP");
            this.add("NAME");
            this.add("TO");
        }
    };

    public static String normFor(final String field,
                                 final Function<String, String> fnTable) {
        final StringBuilder normalized = new StringBuilder();
        if (Objects.nonNull(fnTable)) {
            normalized.append(fnTable.apply(field)).append(".");
        }
        normalized.append(KEYWORD_SET.contains(field) ? "`" + field + "`" : field);
        return normalized.toString();
    }
}

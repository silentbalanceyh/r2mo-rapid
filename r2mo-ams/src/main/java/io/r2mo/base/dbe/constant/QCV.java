package io.r2mo.base.dbe.constant;

import java.util.HashSet;
import java.util.Set;

/**
 * Qr常量文件，Qr专用语法
 * <pre>
 *     {
 *         "criteria": {},
 *         "pager": {
 *             "page": 1,
 *             "size": 10
 *         },
 *         "sorter": [],
 *         "projection": []
 *     }
 * </pre>
 *
 * @author lang : 2025-08-28
 */
public interface QCV {

    String P_PAGER = "pager";
    String P_SORTER = "sorter";
    String P_PROJECTION = "projection";
    String P_CRITERIA = "criteria";

    enum Connector {
        AND,
        OR,
    }

    enum Aggr {
        COUNT,
        SUM,
        AVG,
        MAX,
        MIN
    }

    interface Op {
        String EQ = "=";                    // 等于
        String NEQ = "!=";                  // 不等于
        String GT = ">";                    // 大于
        String GTE = ">=";                  // 大于等于
        String LT = "<";                    // 小于
        String LTE = "<=";                  // 小于等于
        String NULL = "n";                  // 为空
        String NOT_NULL = "!n";             // 不为空
        String IN = "i";                    // 在...之中
        String NOT_IN = "!i";               // 不在...之中
        String START = "s";                 // 以...开始
        String END = "e";                   // 以...结束
        String CONTAIN = "c";               // 包含 / LIKE
        Set<String> VALUES = new HashSet<>() {
            {
                this.add(EQ);
                this.add(NEQ);
                this.add(GT);
                this.add(GTE);
                this.add(LT);
                this.add(LTE);
                this.add(NULL);
                this.add(NOT_NULL);
                this.add(IN);
                this.add(NOT_IN);
                this.add(START);
                this.add(END);
                this.add(CONTAIN);
            }
        };
    }
}

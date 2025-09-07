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
        String NEQ = "<>";                  // 不等于
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
                this.add(Op.EQ);
                this.add(Op.NEQ);
                this.add(Op.GT);
                this.add(Op.GTE);
                this.add(Op.LT);
                this.add(Op.LTE);
                this.add(Op.NULL);
                this.add(Op.NOT_NULL);
                this.add(Op.IN);
                this.add(Op.NOT_IN);
                this.add(Op.START);
                this.add(Op.END);
                this.add(Op.CONTAIN);
            }
        };
    }
}

package io.r2mo.base.dbe.common;

import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.json.JObject;

/**
 * @author lang : 2025-10-24
 */
public interface DBFor {
    Cc<String, DBFor> CCT_DB_FOR = Cc.openThread();

    static DBFor ofC(final boolean isMajor) {
        if (isMajor) {
            return CCT_DB_FOR.pick(DBForMajor::new, DBForMajor.class.getName());
        } else {
            return CCT_DB_FOR.pick(DBForMinor::new, DBForMinor.class.getName());
        }
    }

    JObject exchange(JObject request, DBNode current, DBRef ref);
}

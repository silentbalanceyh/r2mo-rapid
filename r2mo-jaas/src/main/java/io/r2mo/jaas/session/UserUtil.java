package io.r2mo.jaas.session;

import cn.hutool.core.util.StrUtil;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;

class UserUtil {
    static MSUser fromJson(final JObject data) {
        // 提取 id
        final String id = data.getString(UserAt.ID_USER);
        if (StrUtil.isEmpty(id)) {
            return null;
        }
        return SPI.V_UTIL.deserializeJson(data, MSUser.class);
    }
}

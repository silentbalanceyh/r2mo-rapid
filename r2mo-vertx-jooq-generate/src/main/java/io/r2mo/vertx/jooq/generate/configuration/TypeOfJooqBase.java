package io.r2mo.vertx.jooq.generate.configuration;

import cn.hutool.core.util.StrUtil;
import org.jooq.meta.jaxb.ForcedType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class TypeOfJooqBase implements TypeOfJooq {
    @Override
    public List<ForcedType> withForcedTypes() {
        final Map<String, String> metadata = this.withMeta();
        if (Objects.isNull(metadata)) {
            return List.of();
        }
        final List<ForcedType> typeList = new ArrayList<>();
        for (final Map.Entry<String, String> entry : metadata.entrySet()) {
            final String column = entry.getKey();
            final String table = entry.getValue();
            if (StrUtil.isEmpty(column) || StrUtil.isEmpty(table)) {
                continue;
            }

            final Class<?> typeUser = this.withUserType();
            final Class<?> typeConverter = this.withConverter();
            if (Objects.isNull(typeConverter) || Objects.isNull(typeUser)) {
                continue;
            }

            typeList.add(new ForcedType()
                .withUserType(typeUser.getName())
                .withConverter(typeConverter.getName())
                // 生成正则：.*\.TABLE_NAME\.FIELD_NAME
                .withIncludeExpression(String.format(".*\\.%s\\.%s", table, column))
            );
        }
        return typeList;
    }

    protected abstract Class<?> withUserType();

    protected abstract Class<?> withConverter();

    protected abstract Map<String, String> withMeta();
}

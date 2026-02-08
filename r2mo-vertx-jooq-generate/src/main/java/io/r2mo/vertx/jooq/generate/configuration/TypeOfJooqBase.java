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
        final Class<?> typeUser = this.withUserType();
        final Class<?> typeConverter = this.withConverter();
        if (Objects.isNull(typeConverter) || Objects.isNull(typeUser)) {
            return List.of();
        }


        final List<String> regexList = this.regexCombine();
        final List<ForcedType> typeList = new ArrayList<>();
        regexList.forEach(expression -> typeList.add(new ForcedType()
            .withUserType(typeUser.getName())
            .withConverter(typeConverter.getName())
            // 生成正则：.*\.TABLE_NAME\.FIELD_NAME
            .withIncludeExpression(expression)
        ));
        return typeList;
    }

    private List<String> regexCombine() {
        final List<String> expression = new ArrayList<>();
        final Map<String, String> regexMap = this.regexMeta();
        if (Objects.nonNull(regexMap)) {
            for (final Map.Entry<String, String> entry : regexMap.entrySet()) {
                final String column = entry.getKey();
                final String table = entry.getValue();
                if (StrUtil.isEmpty(column) || StrUtil.isEmpty(table)) {
                    continue;
                }
                expression.add(String.format(".*\\.%s\\.%s", table, column));
            }
        }
        final List<String> regexField = this.regexField();
        if (Objects.nonNull(regexField)) {
            for (final String field : regexField) {
                if (StrUtil.isEmpty(field)) {
                    continue;
                }
                expression.add(String.format(".*\\.%s", field));
            }
        }
        final List<String> regexExpression = this.regexExpression();
        if (Objects.nonNull(regexExpression)) {
            for (final String exp : regexExpression) {
                if (StrUtil.isEmpty(exp)) {
                    continue;
                }
                expression.add(exp);
            }
        }
        return expression;
    }

    protected abstract Class<?> withUserType();

    protected abstract Class<?> withConverter();

    protected Map<String, String> regexMeta() {
        return Map.of();
    }

    protected List<String> regexField() {
        return List.of();
    }

    protected List<String> regexExpression() {
        return List.of();
    }
}

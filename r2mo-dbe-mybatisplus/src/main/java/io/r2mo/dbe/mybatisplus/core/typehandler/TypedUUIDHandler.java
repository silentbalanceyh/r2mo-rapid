package io.r2mo.dbe.mybatisplus.core.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

/**
 * @author lang : 2025-08-28
 */
@MappedTypes(UUID.class)
public class TypedUUIDHandler extends BaseTypeHandler<UUID> {

    @Override
    public void setParameter(final PreparedStatement ps,
                             final int i, final UUID parameter, final JdbcType jdbcType) throws SQLException {
        if ((parameter == null) && (jdbcType == JdbcType.OTHER)) {
            ps.setString(i, UUID.randomUUID().toString());
        } else if (Objects.nonNull(parameter)) {
            ps.setString(i, parameter.toString());
        }
    }

    @Override
    public void setNonNullParameter(final PreparedStatement ps,
                                    final int i, final UUID parameter,
                                    final JdbcType jdbcType) throws SQLException {
        // 将 UUID 转换为字符串并且设置到 PreparedStatement
        ps.setString(i, parameter.toString());
    }

    @Override
    public UUID getNullableResult(final ResultSet resultSet, final String columnName)
        throws SQLException {
        // 从 ResultSet 中获取字符串并转换为 UUID
        return this.getResult(resultSet.getString(columnName));
    }

    @Override
    public UUID getNullableResult(final ResultSet resultSet, final int i)
        throws SQLException {
        // 从 ResultSet 中获取字符串并转换为 UUID
        return this.getResult(resultSet.getString(i));
    }

    @Override
    public UUID getNullableResult(final CallableStatement callableStatement, final int i)
        throws SQLException {
        // 从 CallableStatement 中获取字符串并转换为 UUID
        return this.getResult(callableStatement.getString(i));
    }

    private UUID getResult(final String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return UUID.fromString(value);
    }
}

package io.r2mo.dbe.mybatisplus.core.typehandler;

import io.r2mo.typed.json.JBase;
import io.r2mo.typed.json.JObject;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Object 类型处理器（支持 JObject）
 * 用于 MyBatis Plus 中 Object 类型与数据库字段的转换
 *
 * @author lang : 2025-09-08
 */
@MappedTypes(JObject.class)
@MappedJdbcTypes({JdbcType.VARCHAR, JdbcType.LONGVARCHAR, JdbcType.CLOB})
public class TypedJObjectHandler extends BaseTypeHandler<Object> {

    @Override
    public void setNonNullParameter(final PreparedStatement ps, final int i, final Object parameter, final JdbcType jdbcType)
        throws SQLException {
        if (parameter == null) {
            ps.setString(i, null);
            return;
        }

        final String jsonString;
        if (parameter instanceof final JObject parameterJ) {
            // 如果是 JObject 类型，使用 encode() 方法
            jsonString = parameterJ.encode();
        } else if (parameter instanceof final String parameterS) {
            // 如果是 String 类型，直接使用
            jsonString = parameterS;
        } else {
            // 其他类型尝试转换为 JObject 或使用 toString()
            jsonString = parameter.toString();
        }

        ps.setString(i, jsonString);
    }

    @Override
    public Object getNullableResult(final ResultSet rs, final String columnName) throws SQLException {
        final String jsonString = rs.getString(columnName);
        return parseJObject(jsonString);
    }

    @Override
    public Object getNullableResult(final ResultSet rs, final int columnIndex) throws SQLException {
        final String jsonString = rs.getString(columnIndex);
        return parseJObject(jsonString);
    }

    @Override
    public Object getNullableResult(final CallableStatement cs, final int columnIndex) throws SQLException {
        final String jsonString = cs.getString(columnIndex);
        return parseJObject(jsonString);
    }

    private JObject parseJObject(final String jsonString) {
        return JBase.parse(jsonString);
    }
}
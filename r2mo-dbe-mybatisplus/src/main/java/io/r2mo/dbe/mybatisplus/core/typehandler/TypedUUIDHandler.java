package io.r2mo.dbe.mybatisplus.core.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.nio.ByteBuffer;
import java.sql.*;
import java.util.Objects;
import java.util.UUID;

/**
 * 兼容型 UUID TypeHandler：
 * - Java 写入：支持 UUID / CharSequence(含 String) / byte[16]
 * - DB 列：支持 VARCHAR / BINARY(16) / OTHER
 * - 读取：返回 UUID（仅绑定到 UUID 属性上，不绑定 String）
 * <p>
 * 使用建议：
 * 1) 实体字段用 UUID：本 Handler 生效（推荐）。
 * 2) 实体字段用 String：不要让本 Handler 绑定到 String；直接走默认 StringTypeHandler 即可。
 *
 * @author lang
 */
@MappedJdbcTypes({JdbcType.VARCHAR, JdbcType.BINARY, JdbcType.OTHER})
@MappedTypes({UUID.class})
public class TypedUUIDHandler extends BaseTypeHandler<Object> {

    /* ===================== 写入 ===================== */

    /** 宽松解析：支持去掉分隔符的 32 位十六进制 / 标准 36 位 / 大小写均可 */
    private static UUID parseUuidLenient(final String s) throws SQLException {
        if (s == null) return null;
        final String str = s.trim();
        if (str.isEmpty()) return null;

        // 无连字符的 32 位：转为标准形式 xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
        final String hex32 = str.replace("-", "");
        if (hex32.length() == 32) {
            try {
                final long msb = new java.math.BigInteger(hex32.substring(0, 16), 16).longValue();
                final long lsb = new java.math.BigInteger(hex32.substring(16), 16).longValue();
                return new UUID(msb, lsb);
            } catch (final NumberFormatException e) {
                // fall through
            }
        }
        try {
            return UUID.fromString(str);
        } catch (final IllegalArgumentException ex) {
            throw new SQLException("Invalid UUID string: '" + s + "'", ex);
        }
    }

    private static byte[] uuidToBytes(final UUID uuid) {
        final ByteBuffer bb = ByteBuffer.allocate(16);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    private static UUID bytesToUuid(final byte[] bytes) {
        final ByteBuffer bb = ByteBuffer.wrap(bytes);
        final long msb = bb.getLong();
        final long lsb = bb.getLong();
        return new UUID(msb, lsb);
    }

    /* ===================== 读取 ===================== */

    @Override
    public void setNonNullParameter(final PreparedStatement ps, final int i, final Object parameter, final JdbcType jdbcType) throws SQLException {
        // 统一先把入参转成 UUID 或 byte[16]
        if (parameter instanceof final UUID uuid) {
            this.writeUuid(ps, i, uuid, jdbcType);
            return;
        }
        if (parameter instanceof final CharSequence cs) {
            final UUID uuid = parseUuidLenient(cs.toString());
            this.writeUuid(ps, i, uuid, jdbcType);
            return;
        }
        if (parameter instanceof final byte[] bytes) {
            if (bytes.length != 16) {
                throw new SQLException("UUID binary must be 16 bytes, actual=" + bytes.length);
            }
            final UUID uuid = bytesToUuid(bytes);
            this.writeUuid(ps, i, uuid, jdbcType);
            return;
        }
        // 兜底：尝试字符串化
        if (parameter != null) {
            final UUID uuid = parseUuidLenient(parameter.toString());
            this.writeUuid(ps, i, uuid, jdbcType);
            return;
        }
        // 理论到不了这里（NonNull 分支），给出明确异常
        throw new SQLException("Null UUID is not allowed in setNonNullParameter");
    }

    @Override
    public void setParameter(final PreparedStatement ps, final int i, final Object parameter, final JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            // 为空时：根据 JDBC 类型设置 NULL
            if (jdbcType == null) {
                ps.setNull(i, Types.VARCHAR);
            } else {
                switch (jdbcType) {
                    case BINARY -> ps.setNull(i, Types.BINARY);
                    case OTHER -> ps.setNull(i, Types.OTHER);
                    default -> ps.setNull(i, Types.VARCHAR);
                }
            }
            return;
        }
        // 非空仍走兼容分支
        this.setNonNullParameter(ps, i, parameter, jdbcType);
    }

    private void writeUuid(final PreparedStatement ps, final int i, final UUID uuid, final JdbcType jdbcType) throws SQLException {
        Objects.requireNonNull(uuid, "uuid must not be null");
        if (jdbcType == JdbcType.BINARY) {
            ps.setBytes(i, uuidToBytes(uuid));
        } else {
            // VARCHAR / OTHER / null -> 统一写成标准 36 位字符串
            ps.setString(i, uuid.toString());
        }
    }

    @Override
    public Object getNullableResult(final ResultSet rs, final String columnName) throws SQLException {
        return this.readUuid(rs.getObject(columnName));
    }

    /* ===================== 工具 ===================== */

    @Override
    public Object getNullableResult(final ResultSet rs, final int columnIndex) throws SQLException {
        return this.readUuid(rs.getObject(columnIndex));
    }

    @Override
    public Object getNullableResult(final CallableStatement cs, final int columnIndex) throws SQLException {
        return this.readUuid(cs.getObject(columnIndex));
    }

    private Object readUuid(final Object dbVal) throws SQLException {
        if (dbVal == null) return null;

        if (dbVal instanceof UUID) {
            return dbVal;
        }
        if (dbVal instanceof final byte[] bytes) {
            if (bytes.length != 16) {
                throw new SQLException("UUID binary must be 16 bytes, actual=" + bytes.length);
            }
            return bytesToUuid(bytes);
        }
        // 其它一律当字符串解析
        return parseUuidLenient(dbVal.toString());
    }
}

package io.r2mo.vertx.jooq.shared;

import io.vertx.core.json.JsonArray;
import org.jooq.Binding;
import org.jooq.BindingGetResultSetContext;
import org.jooq.BindingGetSQLInputContext;
import org.jooq.BindingGetStatementContext;
import org.jooq.BindingRegisterContext;
import org.jooq.BindingSQLContext;
import org.jooq.BindingSetSQLOutputContext;
import org.jooq.BindingSetStatementContext;
import org.jooq.Converter;
import org.jooq.RenderContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;
import java.util.Objects;

/**
 * @author jensklingsporn
 */
public class ObjectToJsonArrayBinding implements Binding<Object, JsonArray> {

    private static final Converter<Object, JsonArray> CONVERTER = new Converter<Object, JsonArray>() {
        @Override
        public JsonArray from(final Object t) {
            return t == null ? null : new JsonArray(t.toString());
        }

        @Override
        public Object to(final JsonArray u) {
            return u == null ? null : u.encode();
        }

        @Override
        public Class<Object> fromType() {
            return Object.class;
        }

        @Override
        public Class<JsonArray> toType() {
            return JsonArray.class;
        }
    };

    // The converter does all the work
    @Override
    public Converter<Object, JsonArray> converter() {
        return CONVERTER;
    }

    // Rending a bind variable for the binding context's value and casting it to the json type
    @Override
    public void sql(final BindingSQLContext<JsonArray> ctx) {
        // Depending on how you generate your SQL, you may need to explicitly distinguish
        // between jOOQ generating bind variables or inlined literals. If so, use this check:
        // ctx.render().paramType() == INLINED
        final RenderContext context = ctx.render().visit(DSL.val(ctx.convert(this.converter()).value()));
        if (SQLDialect.POSTGRES.equals(ctx.configuration().dialect().family())) {
            context.sql("::json");
        }
    }

    // Registering VARCHAR types for JDBC CallableStatement OUT parameters
    @Override
    public void register(final BindingRegisterContext<JsonArray> ctx) throws SQLException {
        ctx.statement().registerOutParameter(ctx.index(), Types.VARCHAR);
    }

    // Converting the JsonArray to a String value and setting that on a JDBC PreparedStatement
    @Override
    public void set(final BindingSetStatementContext<JsonArray> ctx) throws SQLException {
        ctx.statement().setString(ctx.index(), Objects.toString(ctx.convert(this.converter()).value(), null));
    }

    // Getting a String value from a JDBC ResultSet and converting that to a JsonArray
    @Override
    public void get(final BindingGetResultSetContext<JsonArray> ctx) throws SQLException {
        ctx.convert(this.converter()).value(ctx.resultSet().getString(ctx.index()));
    }

    // Getting a String value from a JDBC CallableStatement and converting that to a JsonArray
    @Override
    public void get(final BindingGetStatementContext<JsonArray> ctx) throws SQLException {
        ctx.convert(this.converter()).value(ctx.statement().getString(ctx.index()));
    }

    // Setting a value on a JDBC SQLOutput (useful for Oracle OBJECT types)
    @Override
    public void set(final BindingSetSQLOutputContext<JsonArray> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    // Getting a value from a JDBC SQLInput (useful for Oracle OBJECT types)
    @Override
    public void get(final BindingGetSQLInputContext<JsonArray> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
}

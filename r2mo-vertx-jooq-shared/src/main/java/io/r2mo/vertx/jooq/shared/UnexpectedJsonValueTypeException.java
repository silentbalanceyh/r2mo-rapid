package io.r2mo.vertx.jooq.shared;

/**
 * A custom exception type that is thrown from JSON converters, instead of {@link ClassCastException}
 * when the expected type is different than the provided type.
 *
 * @author guss77
 */
public class UnexpectedJsonValueTypeException extends ClassCastException {
    private static final long serialVersionUID = 8727637165178779604L;

    public UnexpectedJsonValueTypeException(final String fieldName, final String fieldType, final ClassCastException cause) {
        super("Invalid JSON type provided for field '" + fieldName + "', expecting: " + jsonifyType(fieldType));
    }

    /**
     * Trim field type to something JSON lovers would recognize.
     *
     * @param type The Java type
     *
     * @return a not exactly JSON type, but close
     */
    private static String jsonifyType(final String type) {
        return type
            .replaceAll("\\w+\\.", "") // remove package
            .replace("Json", ""); // handle Vert.x's Json(Object|Array)
    }

}

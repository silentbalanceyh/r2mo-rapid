package io.r2mo.typed.json;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.Serializable;
import java.util.List;

/**
 * @author lang : 2025-08-28
 */
public interface JBase extends Serializable {

    static List<Module> modules() {
        return JBaseUtil.jacksonModules();
    }

    static <T extends JBase> T parse(final String json) {
        return JBaseUtil.parse(json);
    }

    static JsonMapper jackson() {
        return JBaseUtil.jacksonMapper();
    }

    boolean isEmpty();

    <T> T data();

    <T extends JBase> T subset(String... fields);

    <T extends JBase> T copy();

    String encode();

    String encodePretty();

    String encodeYaml();
}

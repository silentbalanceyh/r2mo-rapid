package io.r2mo.typed.json.jackson;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.util.List;

public interface JacksonRegistry {

    void afterBootstrap(SimpleModule module);

    default List<Module> afterModules() {
        return List.of();
    }
}

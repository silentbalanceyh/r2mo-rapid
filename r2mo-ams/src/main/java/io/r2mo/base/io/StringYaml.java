package io.r2mo.base.io;

import java.io.Serializable;

/**
 * @author lang : 2025-09-02
 */
public class StringYaml implements Serializable {

    private final String yaml;

    public StringYaml(final String yaml) {
        this.yaml = yaml;
    }

    @Override
    public String toString() {
        return this.yaml;
    }
}

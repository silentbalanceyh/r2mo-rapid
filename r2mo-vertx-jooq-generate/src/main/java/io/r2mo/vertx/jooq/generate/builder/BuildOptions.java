package io.r2mo.vertx.jooq.generate.builder;

import java.util.EnumSet;
import java.util.Set;

public class BuildOptions {

    private ConverterInstantiationMethod converterInstantiationMethod;
    private Set<BuildFlag> buildFlags;

    public BuildOptions() {
        this(ConverterInstantiationMethod.SINGLETON, EnumSet.noneOf(BuildFlag.class));
    }

    public BuildOptions(final ConverterInstantiationMethod converterInstantiationMethod, final Set<BuildFlag> buildFlags) {
        this.converterInstantiationMethod = converterInstantiationMethod;
        this.buildFlags = buildFlags;
    }

    public ConverterInstantiationMethod getConverterInstantiationMethod() {
        return this.converterInstantiationMethod;
    }

    public Set<BuildFlag> getBuildFlags() {
        return this.buildFlags;
    }

    public BuildOptions withConverterInstantiationMethod(final ConverterInstantiationMethod converterInstantiationMethod) {
        this.converterInstantiationMethod = converterInstantiationMethod;
        return this;
    }

    public BuildOptions withBuildFlags(final Set<BuildFlag> buildFlags) {
        this.buildFlags = buildFlags;
        return this;
    }

    public BuildOptions addBuildFlags(final BuildFlag buildFlag) {
        this.buildFlags.add(buildFlag);
        return this;
    }

    boolean isEnabled(final BuildFlag flag) {
        return this.buildFlags.contains(flag);
    }

    public enum BuildFlag {
        GENERATE_DATA_OBJECT_ANNOTATION
    }

}

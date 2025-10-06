package io.r2mo.io.common;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.r2mo.base.io.HStore;
import io.r2mo.function.Fn;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JBase;
import io.r2mo.typed.json.JObject;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

/**
 * @author lang : 2025-09-02
 */
public abstract class AbstractHStore implements HStore {

    private static final ObjectMapper YAML = new YAMLMapper();

    @Override
    public URL toURL(final File file) {
        if (null == file) {
            return null;
        }
        final Path path = file.toPath();
        return this.toURL(path);
    }

    @Override
    public URL toURL(final Path path) {
        if (null == path) {
            return null;
        }
        return Fn.jvmOr(path.toUri()::toURL, null);
    }

    private String ioYaml(final InputStream in) {
        if (Objects.isNull(in)) {
            throw new IllegalArgumentException("[ R2MO ] 输入 Stream 为 null");
        }
        final JsonNode node = Fn.jvmOr(() -> YAML.readTree(in));
        if (Objects.isNull(node)) {
            throw new IllegalStateException("[ R2MO ] Yaml (in) 解析结果失败，为 null");
        }
        return node.toString();
    }

    private String ioYaml(final String content) {
        if (StrUtil.isEmpty(content)) {
            return null;
        }
        final JsonNode node = Fn.jvmOr(() -> YAML.readTree(content));
        if (Objects.isNull(node)) {
            throw new IllegalStateException("[ R2MO ] Yaml (String) 解析结果失败，为 null");
        }
        return node.toString();
    }

    @Override
    public <T extends JBase> T inYaml(final URL url) {
        final String content = this.ioYaml(this.inStream(url));
        return JBase.parse(content);
    }

    @Override
    public <T extends JBase> T inYaml(final String filename) {
        final String content = this.ioYaml(this.inStream(filename));
        return JBase.parse(content);
    }

    @Override
    public <T extends JBase> T inYaml(final File file) {
        final String content = this.ioYaml(this.inStream(file));
        return JBase.parse(content);
    }

    @Override
    public <T extends JBase> T inYaml(final Path path) {
        final String content = this.ioYaml(this.inStream(path));
        return JBase.parse(content);
    }

    @Override
    public JArray ymlForA(final String yaml) {
        final String parsed = this.ioYaml(yaml);
        return JBase.parse(parsed);
    }

    @Override
    public JObject ymlForJ(final String yaml) {
        final String parsed = this.ioYaml(yaml);
        return JBase.parse(parsed);
    }

    @Override
    public Properties inProperties(final InputStream in) {
        if (Objects.isNull(in)) {
            throw new IllegalArgumentException("[ R2MO ] 输入 Stream 为 null");
        }
        final Properties properties = new Properties();
        Fn.jvmAt(() -> properties.load(in));
        return properties;
    }
}

package io.r2mo.base.exchange;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author lang : 2025-12-05
 */
@Data
@Accessors(fluent = true, chain = true)
public class NormMessage<T> implements UniMessage<T> {

    private final String id;
    @Setter(AccessLevel.NONE)
    private final Map<String, Object> header = new HashMap<>();
    @Setter(AccessLevel.NONE)
    private final Map<String, Object> params = new HashMap<>();
    private String subject;
    private T payload;
    @Setter(AccessLevel.NONE)
    private Set<String> to = new HashSet<>();

    public NormMessage(final String id) {
        this.id = id;
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <H> H header(final String key) {
        return (H) this.header.get(key);
    }

    @Override
    public void addTo(final String... to) {
        this.to.addAll(Arrays.asList(to));
    }

    public NormMessage<T> header(final String key, final Object value) {
        this.header.put(key, value);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V params(final String key) {
        return (V) this.params.get(key);
    }

    public NormMessage<T> params(final String key, final Object value) {
        this.params.put(key, value);
        return this;
    }
}

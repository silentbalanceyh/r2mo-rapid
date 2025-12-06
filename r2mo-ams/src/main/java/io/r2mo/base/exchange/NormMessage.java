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

    private String subject;

    private T payload;

    @Setter(AccessLevel.NONE)
    private Set<String> to = new HashSet<>();

    private Map<String, Object> header = new HashMap<>();

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

    public NormMessage<T> addTo(final String... to) {
        this.to.addAll(Arrays.asList(to));
        return this;
    }

    public NormMessage<T> header(final String key, final Object value) {
        this.header.put(key, value);
        return this;
    }
}

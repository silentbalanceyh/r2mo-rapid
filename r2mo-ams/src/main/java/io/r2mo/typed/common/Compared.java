package io.r2mo.typed.common;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lang : 2025-09-24
 */
@Data
@Accessors(fluent = true)
public class Compared<T> implements Serializable {

    private final List<T> queueC = new ArrayList<>();
    private final List<T> queueU = new ArrayList<>();
    private final List<T> queueD = new ArrayList<>();
}

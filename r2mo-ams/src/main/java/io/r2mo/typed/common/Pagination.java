package io.r2mo.typed.common;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页结构信息
 * <pre>
 *     {
 *         "count": ??,
 *         "list": []
 *     }
 * </pre>
 *
 * @author lang : 2025-08-28
 */
@Data
public class Pagination<T> {

    private List<T> list = new ArrayList<>();

    private long count = 0;
}

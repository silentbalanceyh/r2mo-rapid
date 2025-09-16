package io.r2mo.base.io.transfer;

import java.util.List;
import java.util.UUID;

/**
 * @author lang : 2025-09-15
 */
public interface HTransferService<REQ, RESP, DATA> extends HTransferAction {
    /**
     * 通过传输请求初始化传输响应
     *
     * @param request 传输请求
     *
     * @return 传输响应
     */
    RESP initialize(REQ request);

    /**
     * 通过令牌获取数据列表
     *
     * @param token 令牌
     *
     * @return 数据列表
     */
    List<DATA> data(String token);

    /**
     * 通过ID获取数据列表
     *
     * @param id ID
     *
     * @return 数据列表
     */
    List<DATA> data(UUID id);

}

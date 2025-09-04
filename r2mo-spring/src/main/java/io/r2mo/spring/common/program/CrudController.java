package io.r2mo.spring.common.program;

import io.r2mo.spi.SPI;
import io.r2mo.typed.service.ActOperation;
import io.r2mo.typed.service.ActResponse;
import io.r2mo.typed.service.ActState;
import io.r2mo.typed.webflow.R;
import io.r2mo.typed.webflow.WebRequest;
import io.r2mo.typed.webflow.WebResponse;

/**
 * Crud 专用 Controller
 *
 * @author lang : 2025-09-04
 */
public abstract class CrudController<T, REQ extends WebRequest<T>, RESP extends WebResponse<T>> {

    public R<RESP> requestNew(final REQ request) {
        // 请求转换
        final T entity = request.data();
        // 保存
        final ActResponse<T> executed = this.service().create(entity);
        // 响应
        final RESP response = this.createResponse();
        if (ActState.SUCCESS_201_CREATED == executed.state()) {
            response.data(entity);
            return R.ok(response, SPI.V_STATUS.ok201());
        } else {
            response.data(executed.data());
            return R.ok(response);
        }
    }

    protected abstract ActOperation<T> service();

    protected abstract RESP createResponse();
}

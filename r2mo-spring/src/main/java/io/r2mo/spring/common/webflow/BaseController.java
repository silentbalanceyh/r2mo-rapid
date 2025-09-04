package io.r2mo.spring.common.webflow;

import cn.hutool.core.io.IoUtil;
import io.r2mo.function.Fn;
import io.r2mo.spi.SPI;
import io.r2mo.typed.common.Pagination;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.service.ActOperation;
import io.r2mo.typed.service.ActResponse;
import io.r2mo.typed.service.ActState;
import io.r2mo.typed.webflow.R;
import io.r2mo.typed.webflow.WebRequest;
import io.r2mo.typed.webflow.WebResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Crud 专用 Controller
 *
 * @author lang : 2025-09-04
 */
@Slf4j
public abstract class BaseController<T, REQ extends WebRequest<T>, RESP extends WebResponse<T>> {

    public R<RESP> createSingle(final REQ request) {
        Objects.requireNonNull(request, "[ R2MO ] 请求对象不能为空！");
        // 请求转换
        final T entity = request.data();
        // 保存
        final ActResponse<T> executed = this.service().create(entity);
        // 响应
        final RESP response = this.createResponse();
        if (ActState.SUCCESS_201_CREATED == executed.state()) {
            // 201 旧数据
            response.data(entity);
            return R.ok(response, SPI.V_STATUS.ok201());
        } else {
            // 200 新数据
            response.data(executed.data());
            return R.ok(response);
        }
    }

    public R<RESP> updateSingle(final String id, final REQ request) {
        Objects.requireNonNull(request, "[ R2MO ] 请求对象不能为空！");
        request.id(id); // 双重检查
        // 请求转换
        final T entity = request.data();
        // 更新
        final ActResponse<T> executed = this.service().updateById(id, entity);
        // 响应
        if (ActState.SUCCESS_204_NO_DATA == executed.state()) {
            // 204 无数据
            return R.ok();
        } else {
            // 200 新数据
            final RESP response = this.createResponse();
            response.data(executed.data());
            return R.ok(response);
        }
    }

    public R<T> findSingle(final String id) {
        final ActResponse<T> executed = this.service().findById(id);
        if (ActState.SUCCESS_204_NO_DATA == executed.state()) {
            // 204 无数据
            return R.ok();
        } else {
            // 200 新数据
            return R.ok(executed.data());
        }
    }

    public R<Boolean> removeSingle(final String id) {
        final ActResponse<Boolean> executed = this.service().removeById(id);
        if (ActState.SUCCESS_210_GONE == executed.state()) {
            // 204 无数据
            return R.ok(false);
        } else {
            // 200 新数据
            return R.ok(true);
        }
    }

    public R<Pagination<T>> findPage(final JObject query) {
        Objects.requireNonNull(query, "[ R2MO ] 请求对象不能为空！");
        // 查询
        final ActResponse<Pagination<T>> executed = this.service().findPage(query);
        // 响应
        return R.ok(executed.data());
    }

    public R<List<T>> findAll() {
        final REQ request = this.createRequest();
        final ActResponse<List<T>> executed = this.service().findAll(request.getAppId(), request.getTenantId());
        // 响应
        return R.ok(executed.data());
    }

    public R<Boolean> uploadData(final MultipartFile file) {
        final BaseAttachment<T> serviceAttachment = this.serviceAttachment();
        // 数据转换
        final List<T> imported = serviceAttachment.toMany(file);
        // 批量保存
        final ActResponse<List<T>> executed = this.service().saveBatch(imported);
        return R.ok(ActState.SUCCESS == executed.state());
    }

    @SuppressWarnings("all")
    public void downloadBy(final JObject criteria) {
        Objects.requireNonNull(criteria, "[ R2MO ] 请求对象不能为空！");
        // 查询数据
        final ActResponse<List<T>> executed = this.service().findBy(criteria);
        if (ActState.SUCCESS_204_NO_DATA == executed.state()) {
            log.warn("[ R2MO ] 无数据可供导出！");
            return;
        }


        // 导出
        final HttpServletResponse response = this.createResponse().response();
        if (Objects.isNull(response)) {
            log.error("[ R2MO ] 当前响应对象不可用，无法导出数据！");
            return;
        }


        response.setContentType("application/octet-stream; charset=UTF-8");
        final BaseAttachment<T> serviceAttachment = this.serviceAttachment();
        final InputStream binary = serviceAttachment.toBinary(executed.data());

        // 特殊参数
        final String filename = criteria.getString("filename", UUID.randomUUID().toString());
        response.setHeader("Content-Disposition", "attachment;filename=" + filename);
        Fn.jvmAt(() -> IoUtil.copy(binary, response.getOutputStream()));
    }

    protected abstract ActOperation<T> service();

    protected BaseAttachment<T> serviceAttachment() {
        // TODO:
        throw new UnsupportedOperationException("[ R2MO ] 当前 Controller 不支持上传下载！");
    }

    protected abstract RESP createResponse();

    protected abstract REQ createRequest();
}

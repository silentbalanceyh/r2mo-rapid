package io.r2mo.spring.common.webflow;

import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.AbstractException;
import io.r2mo.typed.service.ActResponse;
import io.r2mo.typed.service.ActState;
import io.r2mo.typed.webflow.R;
import io.r2mo.typed.webflow.WebResponse;
import io.r2mo.typed.webflow.WebState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author lang : 2025-09-09
 */
class PostResponseReply {
    private static final Cc<String, PostResponseReply> CCT_REPLY = Cc.openThread();

    private PostResponseReply() {
    }

    static PostResponseReply of() {
        return CCT_REPLY.pick(PostResponseReply::new);
    }

    <T, RESP extends WebResponse<T>> R<List<RESP>> replyList(
        final ActResponse<List<T>> actionResult, final Supplier<RESP> supplier) {
        // 检查
        final R<List<RESP>> checked = this.replyPredicate(actionResult, new ArrayList<>());
        if (Objects.nonNull(checked)) {
            return checked;
        }


        // 成功
        final ActState state = actionResult.state();
        final WebState webState = SuperMap.webState(state);
        final List<RESP> responseList = new ArrayList<>();
        actionResult.data().forEach(element -> {
            final RESP resp = supplier.get();
            resp.data(element);
            responseList.add(resp);
        });
        return R.ok(responseList, webState);
    }


    <T, RESP extends WebResponse<T>> R<RESP> replySuccess(
        final ActResponse<T> actionResult, final Supplier<RESP> supplier) {
        // 带有异常
        final R<RESP> checked = this.replyPredicate(actionResult, null);
        if (Objects.nonNull(checked)) {
            return checked;
        }


        // 成功返回
        final ActState state = actionResult.state();
        final WebState webState = SuperMap.webState(state);
        final RESP response = supplier.get();
        response.data(actionResult.data());
        return R.ok(response, webState);
    }


    <T> R<T> replySuccess(final ActResponse<T> actionResult) {
        // 检查
        final R<T> checked = this.replyPredicate(actionResult, null);
        if (Objects.nonNull(checked)) {
            return checked;
        }


        // 成功返回
        final ActState state = actionResult.state();
        final WebState webState = SuperMap.webState(state);
        return R.ok(actionResult.data(), webState);
    }


    R<Boolean> replySuccessOr(final ActResponse<Boolean> actionResult) {
        // 检查
        final R<Boolean> checked = this.replyPredicate(actionResult, false);
        if (Objects.nonNull(checked)) {
            return checked;
        }


        // 成功返回
        final ActState state = actionResult.state();
        final WebState webState = SuperMap.webState(state);
        return R.ok(true, webState);
    }


    private <T, O> R<O> replyPredicate(
        final ActResponse<T> actionResult, final O defaultValue) {
        // 带有异常
        final AbstractException error = actionResult.error();
        if (Objects.nonNull(error)) {
            return R.failure(error);
        }

        final ActState state = actionResult.state();
        // 无数据所有模式比较特殊
        if (ActState.SUCCESS_204_NO_DATA == state) {
            if (Objects.isNull(defaultValue)) {
                return R.ok();
            } else {
                return R.ok(defaultValue, SPI.V_STATUS.ok204());
            }
        }
        return null;
    }
}

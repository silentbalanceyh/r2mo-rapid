package io.r2mo.io.component.node;

import io.r2mo.base.io.modeling.StoreChunk;
import io.r2mo.base.io.modeling.StoreDirectory;
import io.r2mo.base.io.modeling.StoreFile;
import io.r2mo.base.io.modeling.StoreNode;
import io.r2mo.base.io.transfer.TransferRequest;
import io.r2mo.base.io.transfer.token.TransferToken;
import io.r2mo.io.modeling.TransferResponse;
import io.r2mo.typed.cc.Cc;

import java.util.List;

/**
 * 这是通用存储处理接口，用于
 * <pre>
 *     1. 从 {@link TransferRequest} 中构造存储对象
 *     2. 从存储对象构造 {@link TransferResponse}
 * </pre>
 * 将行为分为二阶段，可以辅助整个实现流程，针对不同的处理范围有所区别
 *
 * @author lang : 2025-09-17
 */
public interface StoreInit<T> {
    Cc<String, StoreInit<?>> CCT_INIT = Cc.openThread();

    @SuppressWarnings("unchecked")
    static StoreInit<StoreDirectory> ofDirectory() {
        return (StoreInit<StoreDirectory>) CCT_INIT.pick(StoreInitDirectory::new, StoreInitDirectory.class.getName());
    }

    @SuppressWarnings("unchecked")
    static StoreInit<StoreFile> ofFile() {
        return (StoreInit<StoreFile>) CCT_INIT.pick(StoreInitFile::new, StoreInitFile.class.getName());
    }

    @SuppressWarnings("unchecked")
    static StoreInit<StoreNode> ofNode() {
        return (StoreInit<StoreNode>) CCT_INIT.pick(StoreInitNode::new, StoreInitNode.class.getName());
    }

    @SuppressWarnings("unchecked")
    static StoreInit<List<StoreChunk>> ofChunk() {
        return (StoreInit<List<StoreChunk>>) CCT_INIT.pick(StoreInitChunk::new, StoreInitChunk.class.getName());
    }

    @SuppressWarnings("unchecked")
    static StoreInit<TransferToken> ofToken() {
        return (StoreInit<TransferToken>) CCT_INIT.pick(StoreInitToken::new, StoreInitToken.class.getName());
    }

    T input(TransferRequest request);

    TransferResponse output(T node);
}

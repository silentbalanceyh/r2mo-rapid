package io.r2mo.base.io;

import io.r2mo.base.io.modeling.StoreChunk;
import io.r2mo.base.io.modeling.StoreNode;
import io.r2mo.base.io.transfer.HTransferAction;
import io.r2mo.base.io.transfer.HTransferService;
import io.r2mo.base.io.transfer.TransferTokenPool;
import io.r2mo.typed.json.JObject;

import java.io.Serializable;

/**
 * 此处的分类维度没有按照上传下载来区分，而是按照类型来区分，因为不同的文件类型操作的数据结构不同
 * <pre>
 *     1. 文件处理，对应 StoreChunk
 *     2. 大文件处理，对应 StoreChunk
 *     3. 目录处理，对应 StoreNode
 * </pre>
 *
 * @author lang : 2025-09-16
 */
public interface HTransfer extends Serializable {

    String DEFAULT_ID = "spi.io.transfer.DEFAULT";

    <REQ, RESP, ACT extends HTransferService<REQ, RESP, StoreChunk>> ACT serviceOfFile();

    <REQ, RESP, ACT extends HTransferService<REQ, RESP, StoreChunk>> ACT serviceOfLarge();

    <REQ, RESP, ACT extends HTransferService<REQ, RESP, StoreNode>> ACT serviceOfDirectory();

    <REQ, TOKEN, ACT extends HTransferService<REQ, TOKEN, JObject>> ACT serviceToken(TransferTokenPool store);

    <ACT extends HTransferAction> ACT actionProgress();

    <ACT extends HTransferAction> ACT actionStatistics();
}

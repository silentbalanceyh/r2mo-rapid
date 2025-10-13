package io.r2mo.io.component.node;

import io.r2mo.base.io.HStore;
import io.r2mo.base.io.HUri;
import io.r2mo.base.io.modeling.StoreChunk;
import io.r2mo.base.io.transfer.TransferRequest;
import io.r2mo.base.io.transfer.TransferType;
import io.r2mo.io.common.HFS;
import io.r2mo.io.modeling.TransferResponse;
import io.r2mo.spi.SPI;
import io.r2mo.typed.exception.web._501NotSupportException;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * @author lang : 2025-09-17
 */
class StoreInitChunk implements StoreInit<List<StoreChunk>> {
    @Override
    public List<StoreChunk> input(final TransferRequest request) {
        Objects.requireNonNull(request, "[ R2MO ] TransferRequest 不能为空");

        if (!request.getIsMultipart()) {
            throw new _501NotSupportException("[ R2MO ] 此接口不支持目录操作！");
        }

        if (request.getType() == TransferType.DOWNLOAD) {
            final HStore store = SPI.V_STORE;
            final String path = HUri.UT.resolve(store.pHome(), request.getPathTarget());
            final long totalSize = HFS.of().fileSize(path);
            final int chuntCount = this.calculateTotalChunks(totalSize, request.getChunkSize());
            return IntStream.range(0, chuntCount).mapToObj(i -> new StoreChunk(i, totalSize, request)).toList();
        } else {
            return IntStream.range(0, request.getChunkCount().intValue()).mapToObj(i -> new StoreChunk(i, request)).toList();
        }
    }

    private int calculateTotalChunks(final long totalSize, final Long chunkSize) {
        return (int) Math.ceil((double) totalSize / chunkSize);
    }

    @Override
    public TransferResponse output(final List<StoreChunk> chunks) {

        Objects.requireNonNull(chunks, "[ R2MO ] chunks  不能为空");
        final TransferResponse transferResponse = new TransferResponse();
        final StoreChunk storeChunk = chunks.get(0);
        storeChunk.outTo(transferResponse);
        transferResponse.setCountChunk((long) chunks.size());
        transferResponse.setIsMultipart(true);

        return transferResponse;
    }
}

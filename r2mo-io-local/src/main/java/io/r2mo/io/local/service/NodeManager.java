package io.r2mo.io.local.service;

import io.r2mo.base.io.modeling.StoreChunk;
import io.r2mo.base.io.modeling.StoreNode;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-09-18
 */
final class NodeManager {
    private static final ConcurrentMap<UUID, StoreNode> NODE_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentMap<UUID, List<StoreChunk>> CHUNK_MAP = new ConcurrentHashMap<>();

    private static NodeManager INSTANCE;

    private NodeManager() {
    }

    static NodeManager of() {
        synchronized (NodeManager.class) {
            if (Objects.isNull(INSTANCE)) {
                INSTANCE = new NodeManager();
            }
        }
        return INSTANCE;
    }

    void put(final UUID nodeId, final StoreNode node) {
        NODE_MAP.put(nodeId, node);
    }

    void remove(final UUID nodeId) {
        NODE_MAP.remove(nodeId);
    }

    @SuppressWarnings("unchecked")
    <T extends StoreNode> T find(final UUID nodeId) {
        return (T) NODE_MAP.getOrDefault(nodeId, null);
    }


    void put(final UUID nodeId, final List<StoreChunk> chunks) {
        CHUNK_MAP.put(nodeId, chunks);
    }

    void removeChunk(final UUID nodeId) {
        CHUNK_MAP.remove(nodeId);
    }

    List<StoreChunk> findChunk(final UUID nodeId) {
        return CHUNK_MAP.getOrDefault(nodeId, null);
    }
}

package io.r2mo.io.local.transfer;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/**
 * 上传会话
 */

@Data
@AllArgsConstructor
public class ChunkSession {
    private final UUID sessionId;
    private final String token;
}

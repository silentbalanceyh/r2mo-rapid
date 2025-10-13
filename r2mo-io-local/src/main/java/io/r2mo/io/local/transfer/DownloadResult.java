package io.r2mo.io.local.transfer;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DownloadResult {
    private final boolean success;
    private final String fileName;
    private final long fileSize;
    private final String localPath;
    private final String message;
}
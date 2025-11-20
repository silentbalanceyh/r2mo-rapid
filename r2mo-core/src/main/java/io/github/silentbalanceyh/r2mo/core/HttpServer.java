package io.github.silentbalanceyh.r2mo.core;

import java.util.concurrent.CompletableFuture;

/**
 * Core interface for HTTP server abstraction.
 * Provides a unified API for both Vert.x and Spring implementations.
 */
public interface HttpServer {

    /**
     * Start the HTTP server.
     * 
     * @return CompletableFuture that completes when server is started
     */
    CompletableFuture<Void> start();

    /**
     * Stop the HTTP server.
     * 
     * @return CompletableFuture that completes when server is stopped
     */
    CompletableFuture<Void> stop();

    /**
     * Get the port the server is listening on.
     * 
     * @return the port number
     */
    int getPort();

    /**
     * Check if the server is running.
     * 
     * @return true if server is running, false otherwise
     */
    boolean isRunning();
}

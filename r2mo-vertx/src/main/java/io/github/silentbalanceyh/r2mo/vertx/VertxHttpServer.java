package io.github.silentbalanceyh.r2mo.vertx;

import io.github.silentbalanceyh.r2mo.core.HttpRequest;
import io.github.silentbalanceyh.r2mo.core.HttpResponse;
import io.github.silentbalanceyh.r2mo.core.HttpServer;
import io.github.silentbalanceyh.r2mo.core.RouteHandler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Vert.x implementation of HttpServer.
 */
public class VertxHttpServer implements HttpServer {

    private static final Logger logger = LoggerFactory.getLogger(VertxHttpServer.class);

    private final Vertx vertx;
    private final int port;
    private final Map<String, RouteHandler> routes;
    private io.vertx.core.http.HttpServer server;
    private boolean running = false;

    public VertxHttpServer(int port) {
        this.vertx = Vertx.vertx();
        this.port = port;
        this.routes = new HashMap<>();
    }

    /**
     * Add a route handler.
     * 
     * @param path the route path
     * @param handler the handler
     * @return this server for chaining
     */
    public VertxHttpServer addRoute(String path, RouteHandler handler) {
        routes.put(path, handler);
        return this;
    }

    @Override
    public CompletableFuture<Void> start() {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Router router = Router.router(vertx);

        // Register all routes
        routes.forEach((path, handler) -> {
            router.route(path).handler(ctx -> {
                try {
                    HttpRequest request = new VertxHttpRequest(ctx);
                    HttpResponse response = handler.handle(request);

                    // Set status and headers
                    ctx.response().setStatusCode(response.getStatusCode());
                    response.getHeaders().forEach((name, value) -> 
                        ctx.response().putHeader(name, value)
                    );

                    // Send body
                    ctx.response().end(response.getBody());
                } catch (Exception e) {
                    logger.error("Error handling request", e);
                    ctx.response().setStatusCode(500).end("Internal Server Error");
                }
            });
        });

        HttpServerOptions options = new HttpServerOptions().setPort(port);
        server = vertx.createHttpServer(options);

        server.requestHandler(router).listen(ar -> {
            if (ar.succeeded()) {
                running = true;
                logger.info("Vert.x HTTP server started on port {}", port);
                future.complete(null);
            } else {
                logger.error("Failed to start Vert.x HTTP server", ar.cause());
                future.completeExceptionally(ar.cause());
            }
        });

        return future;
    }

    @Override
    public CompletableFuture<Void> stop() {
        CompletableFuture<Void> future = new CompletableFuture<>();

        if (server != null) {
            server.close(ar -> {
                if (ar.succeeded()) {
                    running = false;
                    vertx.close();
                    logger.info("Vert.x HTTP server stopped");
                    future.complete(null);
                } else {
                    logger.error("Failed to stop Vert.x HTTP server", ar.cause());
                    future.completeExceptionally(ar.cause());
                }
            });
        } else {
            future.complete(null);
        }

        return future;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}

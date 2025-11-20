package io.github.silentbalanceyh.r2mo.examples.vertx;

import io.github.silentbalanceyh.r2mo.core.HttpResponse;
import io.github.silentbalanceyh.r2mo.vertx.VertxHttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example application using Vert.x implementation.
 */
public class VertxExampleApp {

    private static final Logger logger = LoggerFactory.getLogger(VertxExampleApp.class);

    public static void main(String[] args) {
        int port = 8080;

        VertxHttpServer server = new VertxHttpServer(port);

        // Add routes
        server.addRoute("/hello", request -> {
            String name = request.getQueryParam("name");
            if (name == null || name.isEmpty()) {
                name = "World";
            }
            return HttpResponse.ok("Hello, " + name + "! (from Vert.x)");
        });

        server.addRoute("/status", request -> 
            HttpResponse.ok("Vert.x server is running")
        );

        server.addRoute("/echo", request -> 
            HttpResponse.ok("Echo: " + request.getBody())
        );

        // Start server
        server.start().thenAccept(v -> 
            logger.info("Vert.x example server is ready on port {}", port)
        ).exceptionally(throwable -> {
            logger.error("Failed to start server", throwable);
            return null;
        });

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down Vert.x server...");
            server.stop().join();
        }));
    }
}

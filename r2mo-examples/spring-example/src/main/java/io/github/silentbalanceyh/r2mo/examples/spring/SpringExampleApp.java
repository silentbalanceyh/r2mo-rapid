package io.github.silentbalanceyh.r2mo.examples.spring;

import io.github.silentbalanceyh.r2mo.core.HttpResponse;
import io.github.silentbalanceyh.r2mo.spring.SpringHttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example application using Spring implementation.
 */
public class SpringExampleApp {

    private static final Logger logger = LoggerFactory.getLogger(SpringExampleApp.class);

    public static void main(String[] args) {
        int port = 8081;

        SpringHttpServer server = new SpringHttpServer(port);

        // Add routes
        server.addRoute("/hello", request -> {
            String name = request.getQueryParam("name");
            if (name == null || name.isEmpty()) {
                name = "World";
            }
            return HttpResponse.ok("Hello, " + name + "! (from Spring)");
        });

        server.addRoute("/status", request -> 
            HttpResponse.ok("Spring server is running")
        );

        server.addRoute("/echo", request -> 
            HttpResponse.ok("Echo: " + request.getBody())
        );

        // Start server
        server.start().thenAccept(v -> 
            logger.info("Spring example server is ready on port {}", port)
        ).exceptionally(throwable -> {
            logger.error("Failed to start server", throwable);
            return null;
        });

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down Spring server...");
            server.stop().join();
        }));
    }
}

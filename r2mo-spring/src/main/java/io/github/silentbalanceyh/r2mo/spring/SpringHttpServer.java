package io.github.silentbalanceyh.r2mo.spring;

import io.github.silentbalanceyh.r2mo.core.HttpRequest;
import io.github.silentbalanceyh.r2mo.core.HttpResponse;
import io.github.silentbalanceyh.r2mo.core.HttpServer;
import io.github.silentbalanceyh.r2mo.core.RouteHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Spring implementation of HttpServer.
 */
public class SpringHttpServer implements HttpServer {

    private static final Logger logger = LoggerFactory.getLogger(SpringHttpServer.class);

    private final int port;
    private final Map<String, RouteHandler> routes;
    private ConfigurableApplicationContext context;
    private boolean running = false;

    public SpringHttpServer(int port) {
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
    public SpringHttpServer addRoute(String path, RouteHandler handler) {
        routes.put(path, handler);
        return this;
    }

    @Override
    public CompletableFuture<Void> start() {
        CompletableFuture<Void> future = new CompletableFuture<>();

        new Thread(() -> {
            try {
                SpringApplication app = new SpringApplication(SpringServerConfig.class);
                app.setDefaultProperties(Map.of("server.port", String.valueOf(port)));
                
                // Pass routes to the config
                SpringServerConfig.setRoutes(routes);
                
                context = app.run();
                running = true;
                logger.info("Spring HTTP server started on port {}", port);
                future.complete(null);
            } catch (Exception e) {
                logger.error("Failed to start Spring HTTP server", e);
                future.completeExceptionally(e);
            }
        }).start();

        return future;
    }

    @Override
    public CompletableFuture<Void> stop() {
        CompletableFuture<Void> future = new CompletableFuture<>();

        if (context != null) {
            new Thread(() -> {
                try {
                    context.close();
                    running = false;
                    logger.info("Spring HTTP server stopped");
                    future.complete(null);
                } catch (Exception e) {
                    logger.error("Failed to stop Spring HTTP server", e);
                    future.completeExceptionally(e);
                }
            }).start();
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

    /**
     * Spring Boot configuration for the HTTP server.
     */
    @SpringBootApplication
    @RestController
    public static class SpringServerConfig {

        private static Map<String, RouteHandler> routes = new HashMap<>();

        public static void setRoutes(Map<String, RouteHandler> routes) {
            SpringServerConfig.routes = routes;
        }

        @RequestMapping("/**")
        public void handleRequest(HttpServletRequest request, HttpServletResponse response) 
                throws IOException {
            String path = request.getRequestURI();
            RouteHandler handler = routes.get(path);

            if (handler == null) {
                // Check for wildcard routes
                handler = routes.get("/*");
            }

            if (handler != null) {
                try {
                    HttpRequest req = new SpringHttpRequest(request);
                    HttpResponse resp = handler.handle(req);

                    response.setStatus(resp.getStatusCode());
                    resp.getHeaders().forEach(response::setHeader);
                    response.getWriter().write(resp.getBody());
                } catch (Exception e) {
                    logger.error("Error handling request", e);
                    response.setStatus(500);
                    response.getWriter().write("Internal Server Error");
                }
            } else {
                response.setStatus(404);
                response.getWriter().write("Not Found");
            }
        }
    }
}

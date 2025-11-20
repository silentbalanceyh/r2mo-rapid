package io.github.silentbalanceyh.r2mo.vertx;

import io.github.silentbalanceyh.r2mo.core.HttpResponse;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for VertxHttpServer.
 */
@ExtendWith(VertxExtension.class)
class VertxHttpServerTest {

    private VertxHttpServer server;
    private WebClient client;
    private static final int TEST_PORT = 8888;

    @BeforeEach
    void setUp(Vertx vertx, VertxTestContext testContext) {
        server = new VertxHttpServer(TEST_PORT);
        client = WebClient.create(vertx);

        server.addRoute("/hello", request -> {
            String name = request.getQueryParam("name");
            return HttpResponse.ok("Hello, " + (name != null ? name : "World") + "!");
        });

        server.addRoute("/echo", request -> 
            HttpResponse.ok("Echo: " + request.getBody())
        );

        server.start()
            .thenAccept(v -> testContext.completeNow())
            .exceptionally(throwable -> {
                testContext.failNow(throwable);
                return null;
            });
    }

    @AfterEach
    void tearDown(VertxTestContext testContext) {
        if (server != null) {
            server.stop()
                .thenAccept(v -> testContext.completeNow())
                .exceptionally(throwable -> {
                    testContext.failNow(throwable);
                    return null;
                });
        }
    }

    @Test
    void testServerStartsAndIsRunning() {
        assertTrue(server.isRunning());
        assertEquals(TEST_PORT, server.getPort());
    }

    @Test
    void testHelloEndpoint(Vertx vertx, VertxTestContext testContext) {
        client.get(TEST_PORT, "localhost", "/hello")
            .send()
            .onComplete(testContext.succeeding(response -> testContext.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("Hello, World!", response.bodyAsString());
                testContext.completeNow();
            })));
    }

    @Test
    void testHelloEndpointWithQueryParam(Vertx vertx, VertxTestContext testContext) {
        client.get(TEST_PORT, "localhost", "/hello?name=R2MO")
            .send()
            .onComplete(testContext.succeeding(response -> testContext.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("Hello, R2MO!", response.bodyAsString());
                testContext.completeNow();
            })));
    }

    @Test
    void testEchoEndpoint(Vertx vertx, VertxTestContext testContext) {
        client.post(TEST_PORT, "localhost", "/echo")
            .sendBuffer(Buffer.buffer("Test Message"))
            .onComplete(testContext.succeeding(response -> testContext.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("Echo: Test Message", response.bodyAsString());
                testContext.completeNow();
            })));
    }
}

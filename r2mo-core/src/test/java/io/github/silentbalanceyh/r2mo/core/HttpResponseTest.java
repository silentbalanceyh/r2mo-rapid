package io.github.silentbalanceyh.r2mo.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HttpResponse.
 */
class HttpResponseTest {

    @Test
    void testOkResponse() {
        HttpResponse response = HttpResponse.ok("Hello World");
        
        assertEquals(200, response.getStatusCode());
        assertEquals("Hello World", response.getBody());
        assertEquals("text/plain", response.getHeader("Content-Type"));
    }

    @Test
    void testNotFoundResponse() {
        HttpResponse response = HttpResponse.notFound();
        
        assertEquals(404, response.getStatusCode());
        assertEquals("Not Found", response.getBody());
    }

    @Test
    void testErrorResponse() {
        HttpResponse response = HttpResponse.error("Something went wrong");
        
        assertEquals(500, response.getStatusCode());
        assertEquals("Something went wrong", response.getBody());
    }

    @Test
    void testCustomResponse() {
        HttpResponse response = HttpResponse.of(201, "Created");
        
        assertEquals(201, response.getStatusCode());
        assertEquals("Created", response.getBody());
    }

    @Test
    void testSetHeader() {
        HttpResponse response = HttpResponse.ok("Test");
        response.setHeader("X-Custom-Header", "CustomValue");
        
        assertEquals("CustomValue", response.getHeader("X-Custom-Header"));
    }

    @Test
    void testSetStatusCode() {
        HttpResponse response = HttpResponse.ok("Test");
        response.setStatusCode(202);
        
        assertEquals(202, response.getStatusCode());
    }

    @Test
    void testSetBody() {
        HttpResponse response = HttpResponse.ok("Original");
        response.setBody("Updated");
        
        assertEquals("Updated", response.getBody());
    }

    @Test
    void testGetHeaders() {
        HttpResponse response = HttpResponse.ok("Test");
        response.setHeader("X-Header-1", "Value1");
        response.setHeader("X-Header-2", "Value2");
        
        var headers = response.getHeaders();
        assertEquals(3, headers.size()); // Content-Type + 2 custom headers
        assertEquals("Value1", headers.get("X-Header-1"));
        assertEquals("Value2", headers.get("X-Header-2"));
    }

    @Test
    void testChaining() {
        HttpResponse response = HttpResponse.ok("Test")
            .setStatusCode(201)
            .setHeader("X-Custom", "Value")
            .setBody("Updated");
        
        assertEquals(201, response.getStatusCode());
        assertEquals("Value", response.getHeader("X-Custom"));
        assertEquals("Updated", response.getBody());
    }
}

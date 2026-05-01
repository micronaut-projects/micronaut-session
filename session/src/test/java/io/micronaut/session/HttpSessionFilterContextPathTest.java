package io.micronaut.session;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.session.Session;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "micronaut.server.context-path", value = "/foo")
@Property(name = "micronaut.session.http.cookie", value = "true")
@Property(name = "micronaut.session.http.cookie-path", value = "/foo")
@Property(name = "spec.name", value = "HttpSessionFilterContextPathTest")
@MicronautTest
class HttpSessionFilterContextPathTest {

    @Test
    void testHttpSessionFilterAppliesToContextPathPrefixedRequests(@Client("/") HttpClient httpClient) {
        BlockingHttpClient client = httpClient.toBlocking();
        HttpResponse<String> response = assertDoesNotThrow(() -> client.exchange(HttpRequest.GET("/foo/demo"), String.class));

        assertEquals(HttpStatus.OK, response.status());
        assertEquals("ok", response.body());
        assertNotNull(response.header(HttpHeaders.SET_COOKIE));
        assertTrue(response.header(HttpHeaders.SET_COOKIE).contains("SESSION"));
        assertTrue(response.header(HttpHeaders.SET_COOKIE).contains("Path=/foo"));
    }

    @Requires(property = "spec.name", value = "HttpSessionFilterContextPathTest")
    @Controller("/demo")
    static class DemoController {
        @Get
        String index(Session session) {
            session.put("demo", "ok");
            return "ok";
        }
    }
}

package io.micronaut.session;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.session.http.HttpSessionFilter;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Property(name = "micronaut.session.filter.regex-pattern", value = "^(?!/assets).*$")
@Property(name = "spec.name", value = "HttpSessionFilterPatternTest")
@MicronautTest
class HttpSessionFilterPatternTest {

    @Test
    void testHttpSessionFilterPatternCanBeConfigured(@Client("/") HttpClient httpClient) {
        BlockingHttpClient client = httpClient.toBlocking();
        Argument<Map<String, Boolean>> arg = Argument.mapOf(String.class, Boolean.class);
        HttpResponse<Map<String, Boolean>> response = assertDoesNotThrow(() -> client.exchange(HttpRequest.GET("/assets"), arg));
        assertNotNull(response.body().get("hitSessionFilter"));
        assertFalse(response.body().get("hitSessionFilter"));
        response = assertDoesNotThrow(() -> client.exchange(HttpRequest.GET("/foobar"), arg));
        assertNotNull(response.body().get("hitSessionFilter"));
        assertTrue(response.body().get("hitSessionFilter"));
    }

    @Requires(property = "spec.name", value = "HttpSessionFilterPatternTest")
    @Controller("/assets")
    static class AssetsController {
        @Get
        Map<String, Boolean> index(HttpRequest<?> request) {
            return Map.of("hitSessionFilter", request.getAttribute(HttpSessionFilter.class.getName(), Boolean.class).orElse(false));
        }
    }

    @Requires(property = "spec.name", value = "HttpSessionFilterPatternTest")
    @Controller("/foobar")
    static class FoobarController {
        @Get
        Map<String, Boolean> index(HttpRequest<?> request) {
            return Map.of("hitSessionFilter", request.getAttribute(HttpSessionFilter.class.getName(), Boolean.class).orElse(false));
        }
    }
}

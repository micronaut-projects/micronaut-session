package io.micronaut.session;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Status;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Property(name = "spec.name", value = "MalformedSessionCookieValueTest")
@MicronautTest
class MalformedSessionCookieValueTest {

    @Test
    void testMalformedSessionCookieValue(@Client("/") HttpClient httpClient) {
        BlockingHttpClient client = httpClient.toBlocking();
        HttpRequest<?> request = HttpRequest.GET("/teapot");
        HttpResponse<?> response = assertDoesNotThrow(() -> client.exchange(request));
        assertEquals(HttpStatus.ACCEPTED, response.status());

        HttpRequest<?> requestWithInvalidCookie = HttpRequest.GET("/teapot").cookie(Cookie.of("SESSION", "_invalid_"));
        HttpClientResponseException ex = assertThrows(HttpClientResponseException.class, () -> client.exchange(requestWithInvalidCookie));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Requires(property = "spec.name", value = "MalformedSessionCookieValueTest")
    @Controller("/teapot")
    static class MalformedSessionCookieValueController {

        @Get
        @Status(HttpStatus.ACCEPTED)
        void index() {
            // no-op
        }
    }
}

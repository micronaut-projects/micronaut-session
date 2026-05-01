package io.micronaut.session;

import io.micronaut.core.order.Ordered;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterPhase;
import io.micronaut.session.http.HttpSessionIdEncoder;
import io.micronaut.session.http.HttpSessionIdResolver;
import io.micronaut.session.http.HttpSessionFilter;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpSessionFilterApiTest {

    @Test
    void httpSessionFilterUsesOrderedMethodBasedFilterApi() {
        assertFalse(HttpServerFilter.class.isAssignableFrom(HttpSessionFilter.class));
        assertTrue(Ordered.class.isAssignableFrom(HttpSessionFilter.class));
        assertEquals(ServerFilterPhase.SESSION.order(), HttpSessionFilter.ORDER);
    }

    @Test
    void httpSessionFilterRetainsLegacyThreeArgumentConstructor() {
        HttpSessionFilter filter = new HttpSessionFilter(
            new NoOpSessionStore(),
            new HttpSessionIdResolver[0],
            new HttpSessionIdEncoder[0]
        );

        assertNotNull(filter);
        assertNotNull(filter.filterRequest(HttpRequest.GET("/demo")));
    }

    private static final class NoOpSessionStore implements SessionStore<Session> {
        @Override
        public Session newSession() {
            throw new UnsupportedOperationException("Not needed for constructor compatibility coverage");
        }

        @Override
        public CompletableFuture<Optional<Session>> findSession(String id) {
            return CompletableFuture.completedFuture(Optional.empty());
        }

        @Override
        public CompletableFuture<Boolean> deleteSession(String id) {
            return CompletableFuture.completedFuture(Boolean.FALSE);
        }

        @Override
        public CompletableFuture<Session> save(Session session) {
            return CompletableFuture.completedFuture(session);
        }
    }
}

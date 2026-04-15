package io.micronaut.session;

import io.micronaut.core.order.Ordered;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterPhase;
import io.micronaut.session.http.HttpSessionFilter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpSessionFilterApiTest {

    @Test
    void httpSessionFilterUsesOrderedMethodBasedFilterApi() {
        assertFalse(HttpServerFilter.class.isAssignableFrom(HttpSessionFilter.class));
        assertTrue(Ordered.class.isAssignableFrom(HttpSessionFilter.class));
        assertEquals(ServerFilterPhase.SESSION.order(), HttpSessionFilter.ORDER);
    }
}

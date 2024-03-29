/*
 * Copyright 2017-2023 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.docs.session;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShoppingControllerTest {

    private static EmbeddedServer server;
    private static HttpClient client;

    @BeforeAll
    public static void setupServer() {
        server = ApplicationContext.run(EmbeddedServer.class);
        client = server
            .getApplicationContext()
            .createBean(HttpClient.class, server.getURL());
    }

    @AfterAll
    public static void stopServer() {
        if (server != null) {
            server.stop();
        }
        if (client != null) {
            client.stop();
        }
    }

    @Test
    void testSessionValueUsedOnReturnValue() {
        // tag::view[]
        HttpResponse<Cart> response = Flux.from(client.exchange(HttpRequest.GET("/shopping/cart"), Cart.class)) // <1>
            .blockFirst();
        Cart cart = response.body();

        assertNotNull(response.header(HttpHeaders.AUTHORIZATION_INFO)); // <2>
        assertNotNull(cart);
        assertTrue(cart.getItems().isEmpty());
        // end::view[]

        // tag::add[]
        String sessionId = response.header(HttpHeaders.AUTHORIZATION_INFO); // <1>

        response = Flux.from(client.exchange(HttpRequest.POST("/shopping/cart/Apple", "")
                .header(HttpHeaders.AUTHORIZATION_INFO, sessionId), Cart.class)) // <2>
            .blockFirst();
        cart = response.body();
        // end::add[]

        assertNotNull(cart);
        assertEquals(1, cart.getItems().size());

        response = Flux.from(client.exchange(HttpRequest.GET("/shopping/cart")
                .header(HttpHeaders.AUTHORIZATION_INFO, sessionId), Cart.class))
            .blockFirst();
        cart = response.body();

        response.header(HttpHeaders.AUTHORIZATION_INFO);

        assertNotNull(cart);
        assertEquals(1, cart.getItems().size());
        assertEquals("Apple", cart.getItems().get(0));
    }
}

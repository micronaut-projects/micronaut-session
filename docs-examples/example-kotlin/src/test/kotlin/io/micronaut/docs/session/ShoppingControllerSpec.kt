/*
 * Copyright 2017-2023 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.docs.session

import io.kotest.matchers.shouldBe
import io.kotest.core.spec.style.StringSpec
import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import reactor.core.publisher.Flux
import kotlin.test.assertNotNull

class ShoppingControllerSpec: StringSpec() {

    val embeddedServer = autoClose(
        ApplicationContext.run(EmbeddedServer::class.java)
    )

    val client = autoClose(
        embeddedServer.applicationContext.createBean(HttpClient::class.java, embeddedServer.url)
    )

    init {
        "testSessionValueUsedOnReturnValue" {
            // tag::view[]
            var response = Flux.from(client.exchange(HttpRequest.GET<Cart>("/shopping/cart"), Cart::class.java)) // <1>
                                 .blockFirst()
            var cart = response.body()

            assertNotNull(response.header(HttpHeaders.AUTHORIZATION_INFO)) // <2>
            assertNotNull(cart)
            cart.items.isEmpty()
            // end::view[]

            // tag::add[]
            val sessionId = response.header(HttpHeaders.AUTHORIZATION_INFO) // <1>

            response = Flux.from(client.exchange(HttpRequest.POST("/shopping/cart/Apple", "")
                             .header(HttpHeaders.AUTHORIZATION_INFO, sessionId), Cart::class.java)) // <2>
                             .blockFirst()
            cart = response.body()
            // end::add[]

            assertNotNull(cart)
            cart.items.size shouldBe 1

            response = Flux.from(client.exchange(HttpRequest.GET<Any>("/shopping/cart")
                             .header(HttpHeaders.AUTHORIZATION_INFO, sessionId), Cart::class.java))
                             .blockFirst()
            cart = response.body()

            response.header(HttpHeaders.AUTHORIZATION_INFO)
            assertNotNull(cart)

            cart.items.size shouldBe 1
            cart.items[0] shouldBe "Apple"
        }
    }
}

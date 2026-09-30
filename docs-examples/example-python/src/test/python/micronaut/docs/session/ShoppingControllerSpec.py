from typing import Annotated

from jakarta.inject import Inject
from micronaut.http import HttpHeaders, HttpRequest
from micronaut.http.client import HttpClient
from micronaut.http.client.annotation import Client
from micronaut.test.extensions.junit5.annotation import MicronautTest
from org.junit.jupiter.api import Test
from reactor.core.publisher import Flux

from .Cart import Cart


@MicronautTest
class ShoppingControllerSpec:
    client: Annotated[HttpClient, Inject, Client("/")]

    @Test
    def test_session_value_used_on_return_value(self) -> None:
        # tag::view[]
        response = Flux.from_(self.client.exchange(HttpRequest.GET("/shopping/cart"), Cart)).blockFirst()  # <1>
        cart = response.body()

        assert response.header(HttpHeaders.AUTHORIZATION_INFO) is not None  # <2>
        assert cart is not None
        assert len(cart.items) == 0
        # end::view[]

        # tag::add[]
        session_id = response.header(HttpHeaders.AUTHORIZATION_INFO)  # <1>

        response = Flux.from_(self.client.exchange(HttpRequest.POST("/shopping/cart/Apple", "")
                                                   .header(HttpHeaders.AUTHORIZATION_INFO, session_id), Cart)).blockFirst()  # <2>
        cart = response.body()
        # end::add[]

        assert cart is not None
        assert len(cart.items) == 1

        response = Flux.from_(self.client.exchange(HttpRequest.GET("/shopping/cart")
                                                   .header(HttpHeaders.AUTHORIZATION_INFO, session_id), Cart)).blockFirst()
        cart = response.body()

        assert response.header(HttpHeaders.AUTHORIZATION_INFO) is not None
        assert cart is not None
        assert len(cart.items) == 1
        assert cart.items[0] == "Apple"

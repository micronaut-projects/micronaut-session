# tag::imports[]
from typing import Annotated

from micronaut.http.annotation import Controller, Get, Post
from micronaut.session import Session
from micronaut.session.annotation import SessionValue

from .Cart import Cart
# end::imports[]


# tag::class[]
ATTR_CART: str = "cart"  # <1>


@Controller("/shopping")
class ShoppingController:
# end::class[]

    # tag::view[]
    @Get("/cart")
    @SessionValue("cart")  # <1>
    def view_cart(self, cart: Annotated[Cart | None, SessionValue]) -> Cart:  # <2>
        if cart is None:
            cart = Cart()
        return cart
    # end::view[]

    # tag::add[]
    @Post("/cart/{name}")
    def add_item(self, session: Session, name: str) -> Cart:  # <2>
        cart = session.get(ATTR_CART, Cart).orElse(None)  # <3>
        if cart is None:
            cart = Cart()
            session.put(ATTR_CART, cart)  # <4>
        cart.items = cart.items + [name]
        return cart
    # end::add[]

    # tag::clear[]
    @Post("/cart/clear")
    def clear_cart(self, session: Session | None) -> None:
        if session is not None:
            session.remove(ATTR_CART)
    # end::clear[]
# tag::endclass[]
# end::endclass[]

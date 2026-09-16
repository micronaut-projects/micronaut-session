# Python Docs Disabled Test Inventory

This file tracks the Python documentation examples under `docs-examples/example-python`
that are present but disabled, or that deviate from the Java example because the direct port does
not compile or does not behave like the Java example yet. It is the bug-fixing task list for
the Python compiler (`micronaut-inject-python` / `micronaut-context-python`); every row references a
`TODO(python)` comment in the sources or a workaround described below.

The Python examples are compiled by every build and their tests run with
`./gradlew pythonCheck -Ppython-ci` (the "Python CI" GitHub workflow).

## Reconciliation

- Last generated active `@Disabled` count: 0.
- Last generated command: `rg -n "@Disabled\(" docs-examples/example-python/src/test/python`.
- Last full-suite command: `./gradlew :micronaut-docs-examples:micronaut-example-python:test -Ppython-ci`.
- Last full-suite result: build successful, 1 test executed, 0 skipped, 0 failures.

## Migration Rules

- Do not define local copies of Micronaut annotation helpers or custom annotation shims in docs snippets.
  Standard Micronaut and session annotations are imported from their Java package
  (`micronaut.http.annotation`, `micronaut.session`, `micronaut.session.annotation`, ...).
- Controller methods are snake_case (`view_cart`, `add_item`, `clear_cart`); the prose quoting the Java
  method name is wrapped in `[.lang-java.lang-kotlin.lang-groovy]` / `[.lang-python]` blocks.
- Nullable parameters are declared with `X | None` (`session: Session | None`,
  `cart: Annotated[Cart | None, SessionValue]`), which emits `@Nullable`.
- Do not add Java-style getters or setters to Python docs models: `Cart` is a `@Serdeable` `@dataclass`
  with an `items: list[str]` attribute.
- The `ATTR_CART` constant is a module-level (typed) constant: a class attribute would be bridged as a
  bean property of the controller with generated accessors. Constants are not used as annotation members
  (the `@SessionValue` annotation of `view_cart` uses the literal `"cart"` like the Groovy example).
- The Python test is a `@MicronautTest` class with an injected `@Client("/") HttpClient`
  (`client: Annotated[HttpClient, Inject, Client("/")]`) instead of the `ApplicationContext.run(EmbeddedServer)`
  / `createBean(HttpClient, url)` setup of the Java example (Python tests must be `@MicronautTest` classes).
- Java classes are imported (`from reactor.core.publisher import Flux`, `from micronaut.http import HttpHeaders`);
  no `java.type(...)` alias is needed by these examples. The examples' own Python class `Cart` works as a runtime
  type argument (`session.get(ATTR_CART, Cart)`, `client.exchange(request, Cart)`).

## Active `@Disabled` Tests

None.

## Commented Unsupported Snippet Ports

None.

## Workarounds Kept In Snippets

| Target | Reason |
| --- | --- |
| `io.micronaut.docs.session.ShoppingController` (`add_item`) | A `Cart` stored in the `Session` (the return value of `view_cart`, or the object passed to `session.put`) comes back from `session.get(ATTR_CART, Cart)` as the generated Java stub (a foreign object exposing only the `items` member), and reading `items` bridges the stub's `List` field as a new copy on every access, so an in-place mutation (`cart.items.append(name)`, `cart.items.add(name)`) is silently lost and the returned cart stays empty. The Python example re-assigns the attribute (`cart.items = cart.items + [name]`), which is written through to the stub, instead of the `cart.getItems().add(name)` of the Java example; a `[.lang-python]` note in `workingWithSessions.adoc` explains the difference. |

## Intentionally Unsupported Snippet Targets

None.

## java.type usages

None.

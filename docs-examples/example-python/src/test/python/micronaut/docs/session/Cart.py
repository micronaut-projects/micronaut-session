from dataclasses import dataclass, field

from micronaut.serde.annotation import Serdeable


@Serdeable
@dataclass
class Cart:
    items: list[str] = field(default_factory=list)

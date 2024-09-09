package model

import cats.data.NonEmptySet
import io.circe.{Decoder, Encoder}
import types.ProductId

final case class Product(id: ProductId, names: NonEmptySet[Translation])

object Product:
  given Decoder[Product] = Decoder.forProduct2("id", "names")(Product.apply)

  given Encoder[Product] = Encoder.forProduct2("id", "names")(p => (p.id, p.names))

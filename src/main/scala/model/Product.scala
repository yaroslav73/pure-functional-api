package model

import cats.data.NonEmptySet
import cats.syntax.all.*
import io.circe.{ Decoder, Encoder }
import types.ProductId

final case class Product(id: ProductId, names: NonEmptySet[Translation])

object Product:
  given Decoder[Product] = Decoder.forProduct2("id", "names")(Product.apply)

  given Encoder[Product] = Encoder.forProduct2("id", "names")(p => (p.id, p.names))

  def fromDatabase(rows: Seq[(ProductId, String, String)]): Option[Product] =
    val product = for {
      (id, code, name) <- rows.headOption
      translation      <- Translation.fromUnsafe(code)(name)
      product          <- Product(id, NonEmptySet.one(translation)).some
    } yield product
    product.map { product =>
      rows.drop(1).foldLeft(product) { (acc, cols) =>
        val (id, code, name) = cols
        Translation.fromUnsafe(code)(name).fold(acc)(translation => acc.copy(names = acc.names.add(translation)))
      }
    }

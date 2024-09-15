package model

import io.circe.refined.*
import io.circe.{ Decoder, Encoder }
import types.{ LanguageCode, ProductName }

final case class Translation(lang: LanguageCode, name: ProductName)

object Translation:
  def fromUnsafe(lang: String)(name: String): Option[Translation] =
    for {
      lang <- LanguageCode.from(lang).toOption
      name <- ProductName.from(name).toOption
    } yield Translation(lang, name)

  given Decoder[Translation] = Decoder.forProduct2("lang", "name")(Translation.apply)

  given Encoder[Translation] = Encoder.forProduct2("lang", "name")(t => (t.lang, t.name))

  given cats.kernel.Order[Translation] with
    def compare(x: Translation, y: Translation): Int =
      x.lang.value.compareTo(y.lang.value)

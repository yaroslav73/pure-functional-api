package model

import io.circe.refined.*
import io.circe.{Encoder, Decoder}
import types.{LanguageCode, ProductName}

final case class Translation(lang: LanguageCode, name: ProductName)

object Translation:
  given Decoder[Translation] = Decoder.forProduct2("lang", "name")(Translation.apply)

  given Encoder[Translation] = Encoder.forProduct2("lang", "name")(t => (t.lang, t.name))

  given cats.kernel.Order[Translation] with
    def compare(x: Translation, y: Translation): Int = 
      x.lang.value.compareTo(y.lang.value)

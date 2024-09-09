package model

import types.{LanguageCode, ProductName}

final case class Translation(lang: LanguageCode, name: ProductName)

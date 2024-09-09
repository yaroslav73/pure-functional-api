package types

import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.MatchesRegex
import eu.timepit.refined.collection.NonEmpty

import java.util.UUID

type LanguageCode = String Refined MatchesRegex["^[a-z]{2}$"]
type ProductName = String Refined NonEmpty

type ProductId = UUID

package types

import eu.timepit.refined.api.{ Refined, RefinedTypeOps }
import eu.timepit.refined.cats.CatsRefinedTypeOpsSyntax
import eu.timepit.refined.string.MatchesRegex
import eu.timepit.refined.collection.NonEmpty

import java.util.UUID

//import cats._
//import cats.syntax.order._
//import eu.timepit.refined._
//import eu.timepit.refined.api._
//import eu.timepit.refined.auto._
//import eu.timepit.refined.cats._
//import eu.timepit.refined.collection._
//import eu.timepit.refined.string._

type LanguageCode = String Refined MatchesRegex["^[a-z]{2}$"]
object LanguageCode extends RefinedTypeOps[LanguageCode, String] with CatsRefinedTypeOpsSyntax

type ProductName = String Refined NonEmpty
object ProductName extends RefinedTypeOps[ProductName, String] with CatsRefinedTypeOpsSyntax

type ProductId = UUID

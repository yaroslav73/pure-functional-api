package impure.db

import cats.data.NonEmptyList
import model.{ Product, Translation }
import slick.basic.{ DatabaseConfig, DatabasePublisher }
import slick.dbio.DBIO
import types.ProductId
import slick.jdbc.JdbcProfile

import java.util.UUID
import scala.concurrent.Future

// TODO: how to map it in Product?
trait Repository:
  def loadProduct(id: ProductId): Future[Seq[(UUID, String, String)]]
  def loadProducts(): DatabasePublisher[(UUID, String, String)]
  def saveProduct(p: Product): Future[List[Int]]
  def updateProduct(p: Product): Future[List[Int]]

object Repository:
  def make(dbConfig: DatabaseConfig[JdbcProfile]): Repository =
    new Repository:
      import dbConfig.profile.api.*
      import eu.timepit.refined.auto.autoUnwrap
      import Tables.{ namesTable, productsTable }

      def loadProduct(id: ProductId): Future[Seq[(ProductId, String, String)]] =
        val query = for {
          (p, ns) <- productsTable
            .filter(_.id == id)
            .join(namesTable)
            .on(_.id === _.productId)
        } yield (p.id, ns.langCode, ns.name)
        dbConfig.db.run(query.result)

      def loadProducts(): DatabasePublisher[(ProductId, String, String)] =
        val query = for {
          (p, ns) <- productsTable
            .join(namesTable)
            .on(_.id === _.productId)
            .sortBy { case (p, _) => p.id }
        } yield (p.id, ns.langCode, ns.name)
        dbConfig.db.stream(query.result)

      def saveProduct(p: Product): Future[List[Int]] =
        val cp    = productsTable += p.id
        val query = DBIO.sequence(cp :: saveTranslations(p).toList).transactionally
        dbConfig.db.run(query)

      def updateProduct(p: Product): Future[List[Int]] =
        val query = namesTable
          .filter(_.productId === p.id)
          .delete
          .andThen(DBIO.sequence(saveTranslations(p).toList))
          .transactionally
        dbConfig.db.run(query)

      private def saveTranslations(p: Product): NonEmptyList[DBIO[Int]] =
        val save = saveTranslation(p.id)(_)
        p.names.toNonEmptyList.map(t => save(t))

      private def saveTranslation(id: ProductId)(t: Translation): DBIO[Int] =
        namesTable.insertOrUpdate((id, t.lang, t.name))

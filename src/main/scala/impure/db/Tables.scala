package impure.db

import slick.jdbc.PostgresProfile.api._
import slick.jdbc.PostgresProfile.Table
import slick.lifted.{ TableQuery, Tag }

import java.util.UUID

object Tables:
  final case class Products(tag: Tag) extends Table[UUID](tag, "products"):
    def id = column[UUID]("id", O.PrimaryKey)

    def * = (id)

  val productsTable = TableQuery[Products]

  final case class Names(tag: Tag) extends Table[(UUID, String, String)](tag, "names"):
    def productId = column[UUID]("product_id")
    def langCode = column[String]("lang_code")
    def name = column[String]("name")

    def pk = primaryKey("pk_names", (productId, langCode))
    def producFk = foreignKey("names_product_id_fk", productId, productsTable)(
      _.id,
      onUpdate = ForeignKeyAction.Cascade,
      onDelete = ForeignKeyAction.Cascade,
    )

    def * = (productId, langCode, name)

  val namesTable = TableQuery[Names]

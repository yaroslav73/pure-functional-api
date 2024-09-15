package impure.api

import impure.db.Repository
import model.Product
import org.apache.pekko.http.scaladsl.model.*
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.server.Directives.*
import com.github.pjfanning.pekkohttpcirce.ErrorAccumulatingCirceSupport.*
import eu.timepit.refined.auto._
import types.ProductId

import scala.concurrent.{ ExecutionContext, Future }

final case class ProductRoutes(repository: Repository)(using ec: ExecutionContext):
  val routes: Route = path("product" / JavaUUID) { (id: ProductId) =>
    get {
      complete {
        for {
          rows    <- repository.loadProduct(id)
          product <- Future { Product.fromDatabase(rows) }
        } yield product
      }
    } ~ put {
      entity(as[Product]) { product =>
        complete {
          repository.updateProduct(product).map { result =>
            HttpResponse(StatusCodes.OK, entity = result.toString)
          }
        }
      }
    }
  }

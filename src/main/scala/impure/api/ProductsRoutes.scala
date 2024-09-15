package impure.api

import cats.syntax.all.*
import impure.db.Repository
import model.Product
import org.apache.pekko.http.scaladsl.model.*
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.server.Directives.*
import com.github.pjfanning.pekkohttpcirce.ErrorAccumulatingCirceSupport.*
import eu.timepit.refined.auto.*
import org.apache.pekko.NotUsed
import org.apache.pekko.http.scaladsl.common.{ EntityStreamingSupport, JsonEntityStreamingSupport }
import org.apache.pekko.stream.scaladsl.Source

import java.util.UUID
import scala.concurrent.ExecutionContext

case class ProductsRoutes(repository: Repository)(using ec: ExecutionContext):
  val routes: Route = path("products") {
    get {
      given JsonEntityStreamingSupport = EntityStreamingSupport.json()

      val f: PartialFunction[(UUID, String, String), Product] = col =>
        Product.fromDatabase(Seq(col)) match
          case Some(p) => p

      val src = Source.fromPublisher(repository.loadProducts())
      val products: Source[Product, NotUsed] =
        src
          .collect(col => f(col))
          .groupBy(Int.MaxValue, (p: Product) => p.id)
          .fold(Option.empty[Product])((op, x) => op.fold(x.some)(p => p.copy(names = p.names ++ x.names).some))
          .mergeSubstreams
          .collect {
            case Some(p) => p
          }

      complete(products)
    } ~ post {
      entity(as[Product]) { product =>
        complete {
          repository.saveProduct(product)
        }
      }
    }
  }

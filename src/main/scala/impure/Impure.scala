package impure

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.http.scaladsl.Http
import org.flywaydb.core.Flyway
import types.ProductId

import scala.concurrent.ExecutionContext
import scala.io.StdIn

object Impure extends App:
  given system: ActorSystem = ActorSystem("impure")
  given ec: ExecutionContext = system.dispatcher

  val dbConfig = system.settings.config.getConfig("database.db.properties")
  val url      = dbConfig.getString("url")
  val user     = dbConfig.getString("user")
  val password = dbConfig.getString("password")

  val flyway = Flyway.configure().dataSource(url, user, password).load()
  val _      = flyway.migrate()

  val apiConfig = system.settings.config.getConfig("api")
  val host      = apiConfig.getString("host")
  val port      = apiConfig.getInt("port")

  // TODO: Extract
  import org.apache.pekko.http.scaladsl.model._
  import org.apache.pekko.http.scaladsl.server.Directives._

  val route = path("pruduct" / JavaUUID) { (id: ProductId) =>
    get {
      ???
    } ~ put {
      ???
    }
  } ~ path("products") {
    get {
      ???
    } ~ post {
      ???
    }
  }

  val server     = Http().newServerAt(host, port).bind(route)
  val pressEnter = StdIn.readLine()

  server
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())

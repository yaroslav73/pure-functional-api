package impure

import org.apache.pekko.actor.ActorSystem
import org.flywaydb.core.Flyway

object Impure extends App:
  given system: ActorSystem = ActorSystem("impure")

  val dbConfig = system.settings.config.getConfig("database.db.properties")
  val url = dbConfig.getString("url")
  val user = dbConfig.getString("user")
  val password = dbConfig.getString("password")

  val flyway = Flyway.configure().dataSource(url, user, password).load()
  val _ = flyway.migrate()

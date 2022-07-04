package common.indexer

import akka.actor.ActorSystem
import akka.stream.alpakka.slick.scaladsl.Slick
import akka.stream.alpakka.slick.scaladsl.SlickSession
import akka.stream.javadsl.Sink
import akka.stream.scaladsl.Source
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigValueFactory
import common.Env

object DB {

  final case class Config(
      endpoint: String,
      database: String,
      username: String,
      password: String
  )

  object Config {
    def fromEnv: Config = {
      val endpoint = Env.getString("INDEXER_DB_ENDPOINT", "localhost:5432")
      val database = Env.getString("INDEXER_DB_DATABASE")
      val username = Env.getString("INDEXER_DB_USERNAME")
      val password = Env.getString("INDEXER_DB_PASSWORD")
      Config(endpoint, database, username, password)
    }
  }

  def session(config: Config): SlickSession = {
    import config._

    val conf =
      s"""
      profile = "slick.jdbc.PostgresProfile$$"
      db {
        dataSourceClass = "slick.jdbc.DriverDataSource"
        properties = {
          driver   = "org.postgresql.Driver"
          url      = "jdbc:postgresql://$endpoint/$database"
          user     = "$username"
          password = "$password"
        }
      }
    """

    SlickSession.forConfig(ConfigFactory.parseString(conf))
  }

  def initSchema(session: SlickSession, filename: String) = {
    import session.profile.api._

    val raw = scala.io.Source
      .fromResource(filename)
      .getLines()
      .foldLeft(new StringBuilder)(_ append _)
      .toString()

    val action =
      DBIO.seq(raw.split(";").map(s => sqlu"#$s;").toSeq: _*)

    session.db.run(action)
  }
}

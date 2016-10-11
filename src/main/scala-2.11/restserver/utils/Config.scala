package restserver.utils

import com.typesafe.config.ConfigFactory

/**
  * Created by anton on 10.10.16.
  */
trait Config {
  private val config = ConfigFactory.load()
  private val httpConfig = config.getConfig("http")
  private val databaseConfig = config.getConfig("database")

  val httpHost = httpConfig.getString("interface")
  val httpPort = httpConfig.getInt("port")

  val dbUrl = databaseConfig.getString("url")
  val dbUser = databaseConfig.getString("user")
  val dbPassword = databaseConfig.getString("password")
  val dbName = databaseConfig.getString("name")
}

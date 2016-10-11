package restserver.db

import reactivemongo.api.MongoDriver
import reactivemongo.core.nodeset.Authenticate
import restserver.utils.Config
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by anton on 10.10.16.
  */
object MongoDB extends Config {

  val driver = new MongoDriver
  val credentials = List(Authenticate(dbName, dbUser, dbPassword))
  val connection = driver.connection(List(dbUrl), authentications = credentials)
  val db = connection(dbName)
}
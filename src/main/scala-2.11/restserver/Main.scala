package restserver

import restserver.utils.Config
import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjackson.JacksonSupport._
import akka.stream.ActorMaterializer
import reactivemongo.bson.BSONObjectID
import restserver.db.{Hotel, HotelsCollection}

import scala.concurrent.ExecutionContext


/**
  * Created by anton on 10.10.16.
  */
object Main extends App with Config{
  implicit val actorSystem = ActorSystem()
  implicit val ec: ExecutionContext = actorSystem.dispatcher
  implicit val logger: LoggingAdapter = Logging(actorSystem, getClass)
  implicit val mat: ActorMaterializer = ActorMaterializer()

  val route =
      pathSingleSlash {
        get {
          complete("<html><body>Hello world!</body></html>")
        }
      } ~
        pathPrefix("json") {
          pathEnd {
            get {
              complete(HotelsCollection.find())
            } ~
            post {
              entity(as[Hotel]) { request =>
                val hotel = if (request.id != null) request else request.copy(BSONObjectID.generate().stringify)
                complete(HotelsCollection.save(hotel))
              }
            }
          } ~
            path("(.*)".r) { id =>
              get {
                complete(HotelsCollection.findById(id))
              }
            }
        }


  Http().bindAndHandle(route, httpHost, httpPort)
}

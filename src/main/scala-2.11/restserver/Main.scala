package restserver

import restserver.utils.Config
import akka.actor.{ActorSystem, Props}
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjackson.JacksonSupport._
import akka.stream.ActorMaterializer
import reactivemongo.bson.BSONObjectID
import restserver.actors.ComputingActor
import restserver.db.{Hotel, HotelsCollection, Location}
import akka.pattern.ask
import restserver.messages.GetNearest

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._


/**
  * Created by anton on 10.10.16.
  */
object Main extends App with Config {
  implicit val actorSystem = ActorSystem()
  implicit val ec: ExecutionContext = actorSystem.dispatcher
  implicit val logger: LoggingAdapter = Logging(actorSystem, getClass)
  implicit val mat: ActorMaterializer = ActorMaterializer()

  val computingActor = actorSystem.actorOf(Props[ComputingActor], "computing-actor")

  val route =
    pathSingleSlash {
      get {
        complete("<html><body>Hello world!</body></html>")
      }
    } ~
      pathPrefix("hotels") {
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
      } ~
      path("nearest") {
        post {
          entity(as[Location]) { location =>
            val f = computingActor.ask(GetNearest(location))(800 milliseconds).mapTo[Hotel]
            complete(f)
          }
        }
      }

  Http().bindAndHandle(route, httpHost, httpPort)
}

package restserver

import restserver.utils.Config
import akka.actor.{ActorSystem, Props}
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjackson.JacksonSupport._
import akka.stream.ActorMaterializer
import reactivemongo.bson.BSONObjectID
import restserver.actors.{CacheDBActor, ComputingRouterActor}
import restserver.db.{Hotel, HotelsCollection, Location}
import akka.pattern.ask
import restserver.messages.GetNearest
import restserver.messages.dboperations._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._


/**
  * Created by anton on 10.10.16.
  */
object Main extends App with Config {
  implicit val actorSystem = ActorSystem("app-system")
  implicit val ec: ExecutionContext = actorSystem.dispatcher
  implicit val logger: LoggingAdapter = Logging(actorSystem, getClass)
  implicit val mat: ActorMaterializer = ActorMaterializer()

  val dbRef= actorSystem.actorOf(Props[CacheDBActor], "cache-router")
  val computingRef= actorSystem.actorOf(Props(new ComputingRouterActor(dbRef)), "computing-router")

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
                dbRef ! Invalidate("hotels")
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
      pathPrefix("cached") {
        pathPrefix("hotels") {
          pathEnd {
            get {
              complete(dbRef.ask(FindAll("hotels"))(5.seconds))
            } ~
              post {
                entity(as[Hotel]) { request =>
                  val hotel = if (request.id != null) request else request.copy(BSONObjectID.generate().stringify)
                  dbRef ! Invalidate("hotels")
                  complete(HotelsCollection.save(hotel))
                }
              }
          } ~
            path("(.*)".r) { id =>
              get {
                complete(dbRef.ask(FindById("hotels", id))(5.seconds))
              }
            }
        }
      } ~
      path("nearest") {
        post {
          entity(as[Location]) { location =>
            complete(computingRef.ask(GetNearest(location))(5.seconds))
          }
        }
      }

  Http().bindAndHandle(route, httpHost, httpPort)
}

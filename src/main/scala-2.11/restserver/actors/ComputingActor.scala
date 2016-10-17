package restserver.actors

import akka.event.Logging
import akka.actor.{Actor, ActorRef}
import restserver.messages.GetNearest
import akka.pattern.ask
import restserver.db.Hotel
import restserver.messages.dboperations.FindAll
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by anton on 12.10.16.
  */
class ComputingActor(db: ActorRef) extends Actor{
  val log = Logging(context.system, this)

  override def preStart() = {
    log.debug("Starting Computing Actor")
  }

  override def receive: Receive = {
    case message: GetNearest => {
      val initiator = sender()
      db.ask(FindAll("hotels"))(2.seconds).mapTo[List[Hotel]].map { result =>
        initiator ! result.minBy(_.location.distance(message.location))
      }
    }
  }
}

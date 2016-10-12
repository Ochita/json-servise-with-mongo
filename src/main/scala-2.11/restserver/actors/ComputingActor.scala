package restserver.actors

import akka.actor.Actor
import restserver.db.HotelsCollection
import restserver.messages.GetNearest
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by anton on 12.10.16.
  */
class ComputingActor extends Actor{
  override def receive: Receive = {
    case message: GetNearest => {
      val initiator = sender()
      HotelsCollection.find() map { hotels =>
        val result = hotels.minBy(_.location.distance(message.location))
        initiator ! result
      }
    }
  }
}

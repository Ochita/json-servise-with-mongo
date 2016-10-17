package restserver.actors

/**
  * Created by anton on 14.10.16.
  */

import akka.actor._
import restserver.messages.dboperations._
import restserver.db.{Hotel, HotelsCollection}
import scala.concurrent.ExecutionContext.Implicits.global


class HotelCollectionCacheActor extends Actor {
  var hotels: List[Hotel] = List()

  override def receive: Receive = {
    case message: FindAll =>
      if (hotels.isEmpty) {
        val initiator = sender()
        HotelsCollection.find() map { results =>
          hotels = results
          initiator ! results
        }
      }
      else
        sender() ! hotels
    case message: FindById =>
      if (hotels.isEmpty) {
        val initiator = sender()
        HotelsCollection.find() map { results =>
          hotels = results
          initiator ! hotels.find(_.id==message.id)
        }
      }
      else
        sender() ! hotels.find(_.id==message.id)
    case message: Invalidate =>
      hotels = List()
  }
}

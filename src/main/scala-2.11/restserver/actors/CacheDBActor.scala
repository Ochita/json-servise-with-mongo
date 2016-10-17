package restserver.actors

/**
  * Created by anton on 14.10.16.
  */

import akka.event.Logging
import akka.actor._
import restserver.messages.dboperations._

class CacheDBActor extends Actor {
  val log = Logging(context.system, this)
  val hotelsCollection = context.actorOf(Props[HotelCollectionCacheActor], "hotel-collection-actor")

  override def receive: Receive = {
    case message: DBOperation =>
      if (message.collection == "hotels")
        hotelsCollection forward message
  }
}

package restserver.actors

import akka.actor.{Actor, ActorRef, Props, Terminated}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}

/**
  * Created by anton on 13.10.16.
  */
class ComputingRouterActor(db: ActorRef) extends Actor {
  var number = 0
  var router = {
    val routees = Vector.fill(4) {
      number = number + 1
      val r = context.actorOf(Props(new ComputingActor(db)), "computing-actor-" + number.toString)
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  def receive = {
    case Terminated(a) =>
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[ComputingActor])
      context watch r
      router = router.addRoutee(r)
    case w =>
      router.route(w, sender())
  }

}

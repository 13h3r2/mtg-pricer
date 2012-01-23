package mtg

import actors.Actor
import actors.scheduler.ForkJoinScheduler

object PrintActorScheduler {
  lazy val sched = {
    val s = new ForkJoinScheduler(2, 2, false, false)
    s.start()
    s
  }

  def getSched = sched
}

class PrintActor(id: String) extends Actor {

  override def scheduler = PrintActorScheduler.getSched

  def act() {
    react {
      case msg =>
        println(id + " start " + msg)
        Thread.sleep(1500 + System.nanoTime() % 1500);
        println(id + " end " + msg)
    }
  }
}


object ActorTest extends App {
  println("start main2")


  (1 to 50).map(i => {
    val actor = new PrintActor(i.toString)
    actor.start
    actor ! "message " + i.toString
    actor
  })
  
  println("all scheduled")

}
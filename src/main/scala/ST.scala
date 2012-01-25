package test

import actors.Actor

class Worker(name: String) extends Actor{



  def act() {
    loop(
      react({
        case _ => println(name + " : " + _ )
      })
    )
    
  }
} 

object ST extends App{
  println("start")
}
package mtg

import ssg.SSGPageSearch

case class Card(name: String, condition: String, price: Double)

//1041 1043 1045 1047 1049 1051 1001
object Test extends App {
  var result: List[Card] = List[Card]()
  for (p <- new SSGPageSearch("1001")) {
    result = result :::(p.getCards.toList)
  }
  println(result)
}


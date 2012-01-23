package mtg

import persistance.CardDAO
import ssg.SSGPageSearch
import com.mongodb.casbah.commons.MongoDBObject

case class Edition(name: String)

case class Card(
                 name: String,
                 condition: String,
                 price: Double) {

  private var edition: Edition = null

  def getEdition = edition

  def setEdition(e: Edition) = {
    edition = e
  }


}

//1041 1043 1045 1047 1049 1051 1001
object Test extends App {

  var result: List[Card] = List[Card]()
  for (p <- new SSGPageSearch("1001")) {
    result = result ::: (p.getCards.toList)
  }

  result foreach {
    CardDAO.save(_)
  }

}


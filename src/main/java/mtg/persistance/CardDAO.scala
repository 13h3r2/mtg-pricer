package mtg.persistance

import mtg.Card
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.commons.MongoDBObject

trait Connection {
  val connection = MongoConnection()("mtg")
}

object CardDAO extends Connection {

  lazy val collection = connection("cards")

  def save(card: Card) = {
    val builder = MongoDBObject.newBuilder
    builder += "name" -> card.name
    builder += "condition" -> card.condition
    builder += "price" -> card.price
    collection.save(builder.result)
  }
}
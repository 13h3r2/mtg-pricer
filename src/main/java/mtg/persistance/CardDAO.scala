package mtg.persistance

import com.mongodb.casbah.commons.MongoDBObject
import mtg.model.{CardPrice, Card}


object CardDAO extends Connection {

  lazy val cardCollection = connection("card")
  lazy val priceCollection = connection("price")

  def savePrice(price: CardPrice) = {

    //save card to card collection if missed
    saveCard(price.card);

    //update price
    val builder = MongoDBObject.newBuilder
    builder += "card_name" -> price.card.name
    builder += "condition" -> price.condition
    builder += "edition_name" -> price.edition

    val searchObject = builder.result();

    builder += "price" -> price.price
    val updateObject = builder.result();

    priceCollection.update(searchObject, updateObject, true, false)
  }

  private def saveCard(card: Card) = {
    val builder = MongoDBObject.newBuilder
    builder += "name" -> card.name

    val exists = cardCollection.findOne(builder.result())
    if (exists.isEmpty) {
      cardCollection.save(builder.result())
    }
  }
}
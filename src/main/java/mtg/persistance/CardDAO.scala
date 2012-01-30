package mtg.persistance

import mtg.model.{CardItem, CardPrice, Card}
import com.mongodb.casbah.commons.{MongoDBList, MongoDBObject}
import com.mongodb.{BasicDBList, DBObject}
import collection.JavaConversions
import org.joda.time.{DateTime, DateMidnight, LocalDate}


object CardDAO extends Connection {

  lazy val cardCollection = conn("card")
  lazy val priceCollection = conn("price")

  def savePrice(price: CardPrice) = {

    val item = findOrCreateCardItem(price.cardItem)
    val priceList = item.get("price").asInstanceOf[BasicDBList]

    if (!JavaConversions.JListWrapper(priceList).exists(x => x.asInstanceOf[DBObject].get("date").asInstanceOf[DateTime].toDateMidnight == DateMidnight.now )) {

      val newPrice = MongoDBObject.newBuilder
      newPrice += "price" -> price.price
      newPrice += "date" -> DateTime.now

      priceList.add(0, newPrice.result())

      priceCollection.update(buildCardItem(price.cardItem).result(), item)
    }
  }

  private def buildCardItem(item: CardItem) = {
    //update price
    val builder = MongoDBObject.newBuilder
    builder += "card_name" -> item.card.name
    builder += "condition" -> item.condition
    builder += "edition_name" -> item.edition.name
    builder
  }

  private def findOrCreateCardItem(item: CardItem): DBObject = {

    //save card to card collection if missed
    saveCard(item.card);

    val builder = buildCardItem(item)

    val found = priceCollection.findOne(builder.result())

    val result = MongoDBObject.newBuilder.result()
    if (found.isEmpty) {
      builder += "price" -> MongoDBList.newBuilder.result()
      priceCollection.save(builder.result())
      result.putAll(priceCollection.findOne(builder.result()).get)
    } else {
      result.putAll(found.get)
    }
    result
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
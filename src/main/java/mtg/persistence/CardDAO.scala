package mtg.persistance

import com.mongodb.casbah.commons.{MongoDBList, MongoDBObject}
import com.mongodb.{BasicDBList, DBObject}
import collection.JavaConversions
import org.joda.time.{DateTime, DateMidnight, LocalDate}
import mtg.model.{PriceSnapshot, CardItem, CardPrice, Card}


object CardDAO extends Connection {

  lazy val cardCollection = conn("card")
  lazy val priceCollection = conn("price")

  def savePrice(card : CardItem, priceSnapshot : PriceSnapshot) = {

  }


}
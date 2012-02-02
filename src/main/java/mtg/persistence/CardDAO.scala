package mtg.persistance

import com.mongodb.casbah.commons.{MongoDBList, MongoDBObject}
import com.mongodb.{BasicDBList, DBObject}
import collection.JavaConversions
import org.joda.time.{DateTime, DateMidnight, LocalDate}
import mtg.model._


object CardDAO extends Connection {

  lazy val cardCollection = conn("card")
  lazy val priceCollection = conn("price")

  def addPriceSnapshot(card : CardItem, priceSnapshot : PriceSnapshot) = {
    import mtg.model.Mapping.CardItemMapping._
    //priceCollection.find(Mapping.CardPriceMapping.item === card).map()
    1
  }


}
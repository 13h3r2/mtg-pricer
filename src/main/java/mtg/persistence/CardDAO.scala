package mtg.persistence

import mtg.model._
import com.mongodb.DBObject
import com.osinka.subset.~._


object CardDAO extends Connection {

  lazy val priceCollection = conn("price")

  def addPriceSnapshot(card: CardItem, priceSnapshot: PriceSnapshot) = {
    import mtg.model.mapping._

    val dbo: Option[DBObject] = priceCollection.findOne(CardPriceMapping.item === card)

    if (dbo.isEmpty) {
      priceCollection.save(new CardPrice(card, priceSnapshot :: Nil))
    } else {
      //calculate diff
      val existingPrice : CardPrice = dbo.map(cpReader.unpack(_).get).get

      if(!existingPrice.prices.isEmpty) {
        priceSnapshot.diff = priceSnapshot.price - existingPrice.prices.last.price
      }

      priceCollection.update(
        CardPriceMapping.item === card,
        CardPriceMapping.price.push(priceSnapshot))
    }
  }
}
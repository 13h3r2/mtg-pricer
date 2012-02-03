package mtg.persistance

import mtg.model._
import com.mongodb.DBObject
import com.osinka.subset.~._


object CardDAO extends Connection {

  lazy val priceCollection = conn("price")

  def addPriceSnapshot(card: CardItem, priceSnapshot: PriceSnapshot) = {
    import mtg.model.Mapping.CardItemMapping._
    import mtg.model.Mapping.CardPriceMapping._
    import mtg.model.Mapping.PriceSnapShotMapping._

    val dbo: Option[DBObject] = priceCollection.findOne(Mapping.CardPriceMapping.item === card)

    if (dbo.isEmpty) {
      priceCollection.save(new CardPrice(card, priceSnapshot :: Nil))
    } else {
      //calculate diff
      val existingPrice : CardPrice = dbo.map(Mapping.CardPriceMapping.cpReader.unpack(_).get).get

      priceSnapshot.diff = priceSnapshot.price - existingPrice.prices.last.price

      priceCollection.update(
        Mapping.CardPriceMapping.item === card,
        Mapping.CardPriceMapping.price.push(priceSnapshot))
    }


  }


}
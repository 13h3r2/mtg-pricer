package mtg.persistence

import mtg.model.mapping._
import com.mongodb.DBObject
import com.osinka.subset._
import math.BigDecimal.RoundingMode
import mtg.model.mapping.PriceSnapShotMapping
import mtg.model.{PriceSnapshot, CardItem}


object CardDAO extends Connection {

  lazy val priceCollection = conn("price2")

  def findLastSnapshot(item: CardItem) = {
    priceCollection
      .find(PriceSnapShotMapping.item === item)
      .sort("date".fieldOf[Int](-1))
      .limit(1)
      .toIterable
      .headOption
      .map(psReader.unpack(_).get)
  }

  def savePriceSnapshot(priceSnapshot: PriceSnapshot): Boolean = {
    import mtg.model.mapping._

    val dbo: Option[DBObject] = priceCollection
      .find(PriceSnapShotMapping.item === priceSnapshot.item)
      .sort("date".fieldOf[Int](-1))
      .limit(1)
      .toIterable
      .headOption


    if (dbo.isEmpty) {
      priceSnapshot.diff = 0
      priceCollection.save(priceSnapshot)
      false
    } else {
      priceSnapshot.diff = BigDecimal(priceSnapshot.price - dbo.get.get("price").asInstanceOf[Double])
        .setScale(2, RoundingMode.HALF_DOWN).toDouble
      if (priceSnapshot.diff.abs >= 0.01) {
        priceCollection.save(priceSnapshot)
      }
      true
    }
  }
}
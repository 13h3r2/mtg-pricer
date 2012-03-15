package mtg.persistence

import mtg.model._
import com.mongodb.DBObject
import com.osinka.subset._
import scala.math
import math.BigDecimal.RoundingMode
import org.apache.commons.lang.time.DateUtils
import java.util.{Calendar, Date}
import mtg.model.mapping.PriceSnapShotMapping


object CardDAO extends Connection {

  lazy val priceCollection = conn("price2")

  def savePriceSnapshot(priceSnapshot: PriceSnapshot) : Boolean = {
    import mtg.model.mapping._

    val dbo: Option[DBObject] = priceCollection
      .find(PriceSnapShotMapping.item === priceSnapshot.item)
      .sort("date".fieldOf[Int](-1))
      .limit(1)
      .toIterable
      .headOption


    if (dbo.isEmpty) {
      false
//      priceSnapshot.diff = 0
//      priceCollection.save(priceSnapshot)
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
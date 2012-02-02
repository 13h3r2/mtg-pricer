package mtg

import com.osinka.subset._
import com.mongodb.casbah.MongoConnection
import java.util.Date
import mtg.model._

object Test2 extends App {


  import Mapping.CardPriceMapping._
  val priceSet = "price".subset(CardPrice).of[CardPrice]


  val obj = new CardPrice(new CardItem("Taiga", "Alpha", "NM"), new PriceSnapshot(10, new Date(), 10) :: new PriceSnapshot(20, new Date(), 10) :: Nil)
  val dbo = priceSet(obj)
  val coll = MongoConnection()("mtg")("price")

  coll.save(obj)
  //coll.save(dbo)

}


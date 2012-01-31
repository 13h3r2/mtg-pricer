package mtg

import com.osinka.subset._
import com.mongodb.casbah.MongoConnection
import com.mongodb.DBObject
import java.util.Date


case class CardItem(name: String, edition: String)

case class PriceSnapShot(price: Double, date: Date)

case class CardPrice(item: CardItem, prices: List[PriceSnapShot])

object CardItemMapping {

  import PriceSnapShotMapping._

  val name = "name".fieldOf[String]
  val edition = "edition".fieldOf[String]
  implicit val ciWriter = ValueWriter[CardItem](ciWriterF _)

  implicit def ciWriterF(obj: CardItem): DBObject = ((name -> obj.name) ~ (edition -> obj.edition))

}

object PriceSnapShotMapping {
  val price = "price".fieldOf[Double]
  val date = "date".fieldOf[Date]
  implicit val pssWriter = ValueWriter[PriceSnapShot](pssWriterF _)

  implicit def pssWriterF(obj: PriceSnapShot): DBObject = ((price -> obj.price) ~ (date -> obj.date))

}

object CardPriceMapping {

  import CardItemMapping._
  import PriceSnapShotMapping._

  val str = "str".fieldOf[String]
  val item = "item".fieldOf[CardItem]
  val price = "price".fieldOf[List[PriceSnapShot]]
  implicit val writer = ValueWriter[CardPrice](writeFunction _)

  implicit def writeFunction(obj: CardPrice): DBObject = ((str -> "a") ~ (item -> obj.item) ~ (price -> obj.prices)
    )

}

object Test2 extends App {

  import CardPriceMapping._

  val priceSet = "price".subset(CardPrice).of[CardPrice]


  val obj = new CardPrice(new CardItem("Taiga", "Alpha"), new PriceSnapShot(10, new Date()) :: new PriceSnapShot(20, new Date()) :: Nil)
  val dbo = priceSet(obj)
  val coll = MongoConnection()("mtg")("card2")

  coll.save(obj)
  //coll.save(dbo)

}


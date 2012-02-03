package mtg.model

import java.util.Date
import com.osinka.subset._
import com.osinka.subset.Mutation._
import com.weiglewilczek.slf4s.Logging
import com.mongodb.{BasicDBObject, DBObject}


case class CardItem(name: String, edition: String, condition: String)

case class PriceSnapshot(price: Double,date: Date) {
  var diff : Double = 0;

  def this(price: Double, date: Date, diff : Double) {
    this (price, date)
    this.diff = diff
  }
}
case class CardPrice(item: CardItem, prices: List[PriceSnapshot])
case class Card(name: String)
case class Edition(name: String, ssgId: String)

object Mapping {


//  object CardMapping {
//    val name = "name".fieldOf[String]
//
//    def fWriter(obj: Card): DBObject = ((name -> obj.name))
//
//    implicit val ciWriter = ValueWriter[Card](CardMapping.fWriter _)
//  }
//
  object EditionMapping {
    val name = "name".fieldOf[String]
    val ssgId = "ssgId".fieldOf[String]
    implicit val ciWriter = ValueWriter[Edition](fWriter _)

    def fWriter(obj: Edition): DBObject = ((name -> obj.name) ~ (ssgId -> obj.ssgId))
  }

  object CardItemMapping {
    val name = "name".fieldOf[String]
    val edition = "edition".fieldOf[String]
    val condition = "condition".fieldOf[String]

    implicit val ciWriter = ValueWriter[CardItem](ciWriterF _)

    implicit def ciWriterF(obj: CardItem): DBObject =
      ((name -> obj.name) ~ (edition -> obj.edition) ~ (condition -> obj.condition) )
  
    implicit var ciReader = ValueReader[CardItem](
      { case name(name) ~ edition(edition) ~ condition(condition) => new CardItem(name, edition, condition) }
    )
  }

  object PriceSnapShotMapping extends Logging {
    import CardItemMapping._
    val price = "price".fieldOf[Double]
    val date = "date".fieldOf[Date]
    val diff = "diff".fieldOf[Double]
    implicit val psWriter = ValueWriter[PriceSnapshot](psWriterF _)

    implicit def psWriterF(obj: PriceSnapshot) : DBObject = ((price -> obj.price) ~ (date -> obj.date) ~ (diff -> obj.diff))
    implicit var psReader = ValueReader[PriceSnapshot]( {
      case (price(p) ~ diff(d) ~ date(da)) => {
        //val result = new PriceSnapshot(p, d, df)
        val result = new PriceSnapshot(p, da, d)
        logger.info("create " + result.toString)
        result
      }
      case x : BasicDBObject => {
        logger.info("wrong type " + x.get("date"))
        logger.info("wrong type " + x.get("date").getClass)
        logger.info("wrong type " + x.toString)
        null
      }
    }
    )
  }

  object CardPriceMapping extends Logging{
    import PriceSnapShotMapping._
    import CardItemMapping._
    val item = "item".fieldOf[CardItem]
    val price = "price".subset(PriceSnapshot).of[List[PriceSnapshot]]
    implicit val cpWriter = ValueWriter[CardPrice](cpWriteFunction _)

    implicit def cpWriteFunction(obj: CardPrice): DBObject = (
      (item -> obj.item) ~ (price -> obj.prices)
    )
    implicit var cpReader = ValueReader[CardPrice]({
        case price(price) ~ item(item) => {
          logger.info("reading card price")
          val result = new CardPrice(item, price)
          logger.info("read card price - " + result.toString)
          result
      }
    })
  }

}


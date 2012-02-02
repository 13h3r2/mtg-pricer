package mtg.model

import com.mongodb.DBObject
import java.util.Date
import com.osinka.subset._
import com.osinka.subset.Mutation._



case class CardItem(name: String, edition: String, condition: String)

case class PriceSnapshot(price: Double, date: Date, diff: Double)

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
//  object EditionMapping {
//    val name = "name".fieldOf[String]
//    val ssgId = "ssgId".fieldOf[String]
//    implicit val ciWriter = ValueWriter[Edition](fWriter _)
//
//    def fWriter(obj: Edition): DBObject = ((name -> obj.name) ~ (ssgId -> obj.ssgId))
//  }

  object CardItemMapping {
    val name = "name".fieldOf[String]
    val edition = "edition".fieldOf[String]
    val condition = "condition".fieldOf[String]
    implicit val ciWriter = ValueWriter[CardItem](ciWriterF _)

    implicit def ciWriterF(obj: CardItem): DBObject = (
      (name -> obj.name) ~ (edition -> obj.edition) ~ (condition -> obj.condition)
    )
  }

  object PriceSnapShotMapping {
    import CardItem._
    val price = "price".fieldOf[Double]
    val date = "date".fieldOf[Date]
    val diff = "diff".fieldOf[Double]
    implicit val ciWriter = ValueWriter[PriceSnapshot](ciWriterF _)

    implicit def ciWriterF(obj: PriceSnapshot) : DBObject = ((price -> obj.price) ~ (date -> obj.date) ~ (diff -> obj.diff))
  }

  object CardPriceMapping {
    import PriceSnapShotMapping._
    import CardItem._
    val str = "str".fieldOf[String]
    val item = "item".fieldOf[CardItem]
    val price = "price".fieldOf[List[PriceSnapshot]]
    implicit val writer = ValueWriter[CardPrice](writeFunction _)
    implicit def writeFunction(obj: CardPrice): DBObject = ((str -> "a") ~ (price -> obj.prices)
      //~ (item -> obj.item)
      )

  }

}


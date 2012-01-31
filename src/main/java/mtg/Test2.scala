package mtg

import com.osinka.subset._
import actions.PriceUpdater
import persistance.EditionDAO
import ssg.SSGPriceProvider
import org.apache.log4j.BasicConfigurator
import com.mongodb.casbah.MongoConnection._
import com.mongodb.casbah.{MongoConnection, MongoDB}
import com.mongodb.DBObject
import mtg.CardPriceMapping._


//case class CardItem(name : String,  edition : String)
case class CardPrice(
                      //                      item : CardItem,
                      price : Double)

object CardPriceMapping {
  //  val item = "item".subset(CardItem).of[CardItem]
  val price = "price".fieldOf[Double]
  implicit val xxx = ValueWriter[CardPrice]({x => x})

  implicit def f(obj: CardPrice): DBObject = ( (CardPriceMapping.price -> obj.price) )
}



//1041 1043 1045 1047 1049 1051 1001
object Test2 extends App {



  //object CardItemMapping {
  //  val cardName = "card_name".fieldOf[String]
  //  val edition = "edition".fieldOf[String]
  //}


  //implicit def writer(obj : CardPrice) = ValueWriter[CardPrice](f _)


  val priceSet = "price".subset(CardPrice).of[CardPrice]

  val dbo = priceSet(new CardPrice(10))
  val coll = MongoConnection()("mtg")("card2")

  coll.save(dbo)
  
}


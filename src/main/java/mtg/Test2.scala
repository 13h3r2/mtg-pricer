package mtg

import com.osinka.subset._
import com.mongodb.casbah.MongoConnection
import java.util.Date
import mtg.model._
import mtg.persistence.CardDAO
import org.apache.log4j.{Level, Logger, BasicConfigurator}

object Test2 extends App {

  BasicConfigurator.configure()
  Logger.getRootLogger.setLevel(Level.INFO)

  //val obj = new CardPrice(new CardItem("Taiga", "Alpha", "NM"), new PriceSnapshot(10, new Date(), 10) :: new PriceSnapshot(20, new Date(), 10) :: Nil)
  //val coll = MongoConnection()("mtg")("price")

  //coll.save(obj)
  //coll.save(dbo)

  CardDAO.addPriceSnapshot(new CardItem("Taiga", "Alpha", "NM"), new PriceSnapshot(15, new Date()) );

}


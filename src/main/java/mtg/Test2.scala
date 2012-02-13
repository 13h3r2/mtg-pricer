package mtg

import actions.{PriceUpdateCommand, PriceUpdater}
import com.osinka.subset._
import com.mongodb.casbah.MongoConnection
import java.util.Date
import mtg.model._
import mtg.persistence.CardDAO
import org.apache.log4j.{Level, Logger, BasicConfigurator}
import ssg.SSGPriceProvider

object Test2 extends App {

  BasicConfigurator.configure()
  CardDAO.savePriceSnapshot(new PriceSnapshot(new CardItem("Bayou", "Alpha", "NM/M"), 100, new Date()))
}


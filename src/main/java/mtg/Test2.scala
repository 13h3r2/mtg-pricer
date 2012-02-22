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
  //val pp = new SSGPriceProvider(new Edition("a", 5173));
}


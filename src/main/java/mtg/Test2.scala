package mtg

import actions.{PriceUpdateCommand, PriceUpdater}
import com.osinka.subset._
import com.mongodb.casbah.MongoConnection
import java.util.Date
import mtg.model._
import org.apache.log4j.{Level, Logger, BasicConfigurator}
import persistence.{Connection, CardDAO}
import ssg.SSGPriceProvider

object Test2 extends App with Connection {

  BasicConfigurator.configure()
  conn("edition").find().foreach(println(_));
}


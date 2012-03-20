package mtg

import actions.{PriceUpdateCommand, PriceUpdater}
import api.MtgRuPriceProcessor
import com.osinka.subset._
import com.mongodb.casbah.MongoConnection
import java.util.Date
import mtg.model._
import org.apache.log4j.{Level, Logger, BasicConfigurator}
import persistence.{Connection, CardDAO}
import ssg.SSGPriceProvider
import actors.Actor
import mtg.model.mapping._
import com.mongodb.DBObject
import io.Source

object Test2 extends App with Connection {

  BasicConfigurator.configure()


  val str = Source.fromFile("1.csv").mkString
  val missed = new MtgRuPriceProcessor().process(str)
  println(missed)
//  new SSGPriceProvider(new Edition("X", "5042", Nil)).getPrice
  //conn("edition").find().foreach(println(_));
}


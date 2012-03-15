package mtg

import actions.PriceUpdater
import model.CardItem
import model.mapping.PriceSnapShotMapping
import org.apache.log4j.BasicConfigurator
import java.lang.{Boolean => BOOL}
import com.mongodb.{DBObject, BasicDBObject}
import mtg.model.mapping._
import persistence.{EditionDAO, Connection}
import ssg.SSGPriceProvider

object Test3 extends App with Connection {

  BasicConfigurator.configure()
    EditionDAO.findAll(1).foreach(e => {
      PriceUpdater.updatePrice(new SSGPriceProvider(e))
    })
//
//  val i = new CardItem("Fork", "3rd Edition/Revised", "SP", false)
//  val filter = new BasicDBObject();
//  var ed : BasicDBObject = new BasicDBObject()
//  ed = ed.append("condition", "SP")
//  ed = ed.append("edition", "Alpha")
//  ed = ed.append("name", "Fork")
//  ed = ed.append("foil", false)
//  println( ed)
//  ed = new BasicDBObject()
//    .append("condition", "SP")
//    .append("edition", "Alpha")
//    .append("name", "Fork")
//    .append("foil", false)
//  println (ed)
//
//
//  printMutation(PriceSnapShotMapping.item === new CardItem("Fork", "Alpha","SP", false))
//
//  filter.append("item", ed);
//  conn("price2")
//    .find(PriceSnapShotMapping.item === new CardItem("Fork", "Alpha","SP", false))
//    .foreach(println(_))
//
//
//  def printMutation(implicit m : DBObject ) {
//    println(m)
//  }
}
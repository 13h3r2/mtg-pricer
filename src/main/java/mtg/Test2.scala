package mtg

import actions.{PriceUpdateCommand, PriceUpdater}
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

object EditionAliasUpdated extends Actor with Connection {
  def act() {
    react {
      case (ed: Edition, alias: String) => {
        conn("edition")
          .findOne(EditionMapping.name(ed.name))
          .map(obj2 => {edReader.unpack(obj2)})
          .foreach(opt => {
            opt.foreach( ed => {
              if( ed.alias.contains(ed.name))
                ed.alias ::= ed.name
              ed.name = alias
            })
          })
      }
    }
  }
}

object Test2 extends App with Connection {

  BasicConfigurator.configure()
  conn("edition").find().foreach(println(_));
}


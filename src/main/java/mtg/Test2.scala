package mtg

import actions.RebuildMonthPriceChanges
import api.MtgRuPriceProcessor
import org.apache.log4j.BasicConfigurator
import io.Source
import com.mongodb.casbah.MongoConnection._
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.commons.MongoDBObjectBuilder
import persistence.{DatabaseUtil, Connection}

object Test2 extends App with Connection {

  RebuildMonthPriceChanges.doIt()
}


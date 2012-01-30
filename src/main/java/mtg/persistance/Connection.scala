package mtg.persistance

import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.commons.conversions.scala.RegisterJodaTimeConversionHelpers
import org.bson.Transformer
import org.scala_tools.time.TypeImports._

trait Connection {
  lazy val conn = {
    RegisterJodaTimeConversionHelpers();
    MongoConnection()("mtg")
  }
}


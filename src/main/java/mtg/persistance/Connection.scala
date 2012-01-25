package mtg.persistance

import com.mongodb.casbah.MongoConnection

trait Connection {
  lazy val connection = MongoConnection()("mtg")
}


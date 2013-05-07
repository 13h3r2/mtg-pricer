package mtg.persistence

import com.mongodb.casbah.{MongoDB, MongoConnection}

trait Connection {

  lazy val conn = {
    MongoConnection()("mtg")
  }

  def withConnection[T](f : MongoDB => T) : T = {
    val connection = MongoConnection()
    try {
      return f(connection("mtg"))
    } finally {
      connection close
    }
  }
}


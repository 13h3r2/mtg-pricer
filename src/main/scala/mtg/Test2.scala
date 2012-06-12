package mtg

import actions.RebuildMonthPriceChanges
import api.MtgRuPriceProcessor
import model.Edition
import org.apache.log4j.BasicConfigurator
import io.Source
import com.mongodb.casbah.MongoConnection._
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.commons.MongoDBObjectBuilder
import actors.{Actor, IScheduler}
import com.weiglewilczek.slf4s.Logging
import persistence.{CardDAO, DatabaseUtil, Connection}
import ssg.SSGPriceProvider
import actors.scheduler.ForkJoinScheduler
import java.net.URL
import org.apache.commons.io.IOUtils
import java.io.{ByteArrayOutputStream, File, FileOutputStream}

object Test2 extends App with Connection {
  val chars = '_' :: '-' :: ('a' to 'z').toList ::: ('A' to 'Z').toList ::: ('0' to '9').toList
  val sch = PrintActorScheduler.newScheduler()
//NFsQG4Q6dzMZw8wgThMowgcU-AOwDXz_rbbpNxnsxjF

//  chars.foreach(a4 => {
    chars.foreach(a3 => {
      chars.foreach(a2 =>
        chars.foreach(a1 => {
          val a = new ImageFetch(sch)
          a.start()
          a ! ("NFsQG4Q6dzMZw8wgThMowgcU-AOwDXz_rbbpNxns" + a1 + a2 + a3)
        })
      )
    })
//  })
}

class ImageFetch(sch: IScheduler) extends Actor with Logging {
  override def scheduler = sch

  def act {
    react {
      case id: String =>
        val url: URL = new URL("http://sales.starcitygames.com/price_icons.php?id=" + id)
        val is = url.openConnection().getInputStream()
        val b = new ByteArrayOutputStream()
        IOUtils.copy(is, b)
        if (b.toByteArray.length > 500) {
          val os = new FileOutputStream("out2/" + id + ".png")
          IOUtils.write(b.toByteArray, os)
          os.close()

          val os2 = new FileOutputStream("out2/" + id + ".txt")
          IOUtils.write(id.getBytes, os2)
          os2.close()
        }
        println(id + " " + b.toByteArray.length)
        b.close()
        is.close()

    }
  }
}

object PrintActorScheduler {
  def newScheduler(): IScheduler = {
    val s = new ForkJoinScheduler(40, 40, false, false)
    s.start()
    s
  }
}

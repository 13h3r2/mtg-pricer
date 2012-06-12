package mtg

import org.apache.log4j.BasicConfigurator
import org.apache.commons.io.IOUtils
import java.io.{FileInputStream, File}
import ssg.SSGOCR


/**
 * cat  /tmp/1 | sed -r s/\ *\(.*\)/\\1/ | sed s/\<\\/a\>\<br\>//
 */
object Test extends App {
  BasicConfigurator.configure
  //    new SSGPriceProvider(new Edition("", "5221", Nil))
  //      .prices
  //      .foreach(println _)
  new File("/home/alexey/git/ssg-break/images")
    .list()
    .filter(_.endsWith(".png"))
    .foreach(f => {
    try {
      SSGOCR.doOCR(IOUtils.toByteArray(new FileInputStream("/home/alexey/git/ssg-break/images/" + f)))
    }
    catch {
      case f => {
        println(f)
      }
    }
  })
}


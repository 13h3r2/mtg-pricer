package mtg

import actions.RebuildMonthPriceChanges
import org.apache.log4j.BasicConfigurator


/**
 * cat  /tmp/1 | sed -r s/\ *\(.*\)/\\1/ | sed s/\<\\/a\>\<br\>//
 */
object Test {
  def main(args: Array[String]) = {
    BasicConfigurator.configure
    RebuildMonthPriceChanges.doIt
  }

  //      new SSGPriceProvider(new Edition("", "5221", Nil))
  //        .prices
  //        .foreach(println _)

  //  new File("/home/alexey/git/ssg-break/images")
  //    .list()
  //    .filter(_.endsWith(".png"))
  //    .foreach(f => {
  //    try {
  //      SSGOCR.doOCR(IOUtils.toByteArray(new FileInputStream("/home/alexey/git/ssg-break/images/" + f)))
  //    }
  //    catch {
  //      case f => {
  //        println(f)
  //      }
  //    }
  //  })
}


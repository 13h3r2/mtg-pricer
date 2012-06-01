package mtg

import actions.PriceUpdater.UpdateActor
import model.Edition
import org.apache.log4j.BasicConfigurator
import ssg.SSGPriceProvider


/**
 * cat  /tmp/1 | sed -r s/\ *\(.*\)/\\1/ | sed s/\<\\/a\>\<br\>//
 */
object Test extends App {
  BasicConfigurator.configure
  val updateActor = new UpdateActor

  new SSGPriceProvider(new Edition("", "5221", Nil))
    .prices
    .foreach(println _)


}


package mtg

import actions.{PriceUpdater}
import model.PriceUpdateAction
import persistence.{PriceUpdateActionDAO, EditionDAO}
import ssg.SSGPriceProvider
import org.apache.log4j.BasicConfigurator
import java.util.Date


/**
 * cat  /tmp/1 | sed -r s/\ *\(.*\)/\\1/ | sed s/\<\\/a\>\<br\>//
 */


//1041 1043 1045 1047 1049 1051 1001
object Test extends App {
  BasicConfigurator.configure
  PriceUpdater update

}


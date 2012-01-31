package mtg

import actions.PriceUpdater
import persistance.EditionDAO
import ssg.SSGPriceProvider
import org.apache.log4j.BasicConfigurator


/**
 * cat  /tmp/1 | sed -r s/\ *\(.*\)/\\1/ | sed s/\<\\/a\>\<br\>//
 */


//1041 1043 1045 1047 1049 1051 1001
object Test extends App {
  BasicConfigurator.configure
  EditionDAO.findAll(10000).foreach(e => {
    PriceUpdater.updatePrice(new SSGPriceProvider(e))
  })
}


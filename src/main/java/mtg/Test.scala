package mtg

import actions.PriceUpdater
import model.Edition
import persistance.EditionDAO
import ssg.SSGPriceProvider


/**
 * cat  /tmp/1 | sed -r s/\ *\(.*\)/\\1/ | sed s/\<\\/a\>\<br\>//
 */


//1041 1043 1045 1047 1049 1051 1001
object Test extends App {
  EditionDAO.findAll(100000).foreach(e => {
    PriceUpdater.updatePrice(new SSGPriceProvider(e))
  })

}


package mtg

import actions.RebuildMonthPriceChanges
import org.apache.log4j.BasicConfigurator
import persistence.Connection

object Test3 extends App  {

  BasicConfigurator.configure
  RebuildMonthPriceChanges.doIt
}
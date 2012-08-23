package mtg

import actions.RebuildMonthPriceChanges
import org.apache.log4j.BasicConfigurator
import persistence.{DatabaseUtil, Connection}

object Test3 extends App  {
  DatabaseUtil.copyDB("mtg", "mtg-back")
}
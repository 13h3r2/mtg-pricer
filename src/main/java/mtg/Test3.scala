package mtg

import actions.PriceUpdater
import api.MtgRuPriceProcessor
import model.mapping.PriceSnapShotMapping
import org.apache.log4j.BasicConfigurator
import io.Source
import mtg.model.mapping._
import persistence.{EditionDAO, Connection}
import ssg.SSGPriceProvider


object Test3 extends App with Connection {

  BasicConfigurator.configure()
  EditionDAO.findAll(1).foreach(e => {
    PriceUpdater.updatePrice(new SSGPriceProvider(e))
  })
}
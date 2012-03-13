package mtg

import api.MtgRuPriceProcessor
import model.mapping.PriceSnapShotMapping
import persistence.Connection
import org.apache.log4j.BasicConfigurator
import io.Source
import mtg.model.mapping._


object Test2 extends App with Connection {

  BasicConfigurator.configure()

  conn("price2")
    .find(PriceSnapShotMapping.item.where(CardItemMapping.foil === true))
    .limit(100)
    .map(psReader.unpack(_))
    .map(_.head)
    .foreach(println(_))
}
package mtg.actions

import mtg.persistence.CardDAO
import actors.Actor
import actors.scheduler.ForkJoinScheduler
import com.weiglewilczek.slf4s.Logging
import mtg.model.{PriceSnapshot, CardItem}
import mtg.ssg.SSGPriceProvider
import mtg.persistence.{PriceUpdateActionDAO, EditionDAO}
import mtg.model.PriceUpdateAction
import java.util.Date

object PriceUpdateCommand {
  def doIt() = {
    EditionDAO.findAll(10000).foreach(e => {
      PriceUpdater.updatePrice(new SSGPriceProvider(e))
    })
    PriceUpdateActionDAO.insert(new PriceUpdateAction(new Date()))
  }
}


trait PriceProvider {
  def getPrice: Set[(CardItem, PriceSnapshot)]
}

object PriceUpdater extends Logging {

  def updatePrice(provider: PriceProvider) {
    val actor = new UpdateActor()
    actor.start()
    actor ! provider
  }

  class UpdateActor() extends Actor {

    override def scheduler = PrintActorScheduler.getSched

    def act() {
      react {
        case provider: PriceProvider =>
          logger.debug("start " + provider)
          provider.getPrice.foreach(obj => CardDAO.addPriceSnapshot(obj._1, obj._2))
          logger.debug("end " + provider)
      }
    }
  }

  object PrintActorScheduler {
    lazy val sched = {
      val s = new ForkJoinScheduler(10, 10, false, false)
      s.start()
      s
    }

    def getSched = sched
  }

}
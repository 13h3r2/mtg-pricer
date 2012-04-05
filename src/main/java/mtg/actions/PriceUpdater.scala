package mtg.actions

import mtg.persistence.CardDAO
import actors.scheduler.ForkJoinScheduler
import com.weiglewilczek.slf4s.Logging
import mtg.ssg.SSGPriceProvider
import mtg.persistence.{PriceUpdateActionDAO, EditionDAO}
import java.util.Date
import actors.{IScheduler, Actor}
import mtg.actions.PriceUpdater.PrintActorScheduler
import mtg.model.{Edition, PriceSnapshot, PriceUpdateAction}

object PriceUpdateCommand {
  def doIt() = {
    PriceUpdater.update(EditionDAO.findAll(10000))
    PriceUpdateActionDAO.insert(new PriceUpdateAction(new Date()))
  }
}


trait PriceProvider {
  def getPrice: Set[PriceSnapshot]
}

object PriceUpdater extends Logging {

  def update(provider: Iterator[Edition]) {
    val sch = PrintActorScheduler.newScheduler()
    provider.foreach(a => PriceUpdater.updatePrice(new SSGPriceProvider(a), sch))
  }

  def updatePrice(provider: PriceProvider, sch: IScheduler) {
    val actor = new UpdateActor(sch)
    actor.start()
    actor ! provider
  }

  class UpdateActor(sch: IScheduler) extends Actor {

    override def scheduler = sch

    def act() {
      react {
        case provider: PriceProvider =>
          logger.debug("start " + provider)
          provider
            .getPrice
            .filter(obj => !CardDAO.savePriceSnapshot(obj))
            .foreach(a => logger.debug("unable to save " + a))
          logger.debug("end " + provider)
      }
    }
  }

  object PrintActorScheduler {
    def newScheduler(): IScheduler = {
      logger.debug("create scheduler")
      val s = new ForkJoinScheduler(10, 10, true, false)
      s.start()
      s
    }
  }

}
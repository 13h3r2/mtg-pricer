package mtg.actions

import mtg.persistence.CardDAO
import actors.scheduler.ForkJoinScheduler
import com.weiglewilczek.slf4s.Logging
import mtg.model.PriceSnapshot
import mtg.ssg.SSGPriceProvider
import mtg.persistence.{PriceUpdateActionDAO, EditionDAO}
import mtg.model.PriceUpdateAction
import java.util.Date
import actors.{IScheduler, Actor}
import mtg.actions.PriceUpdater.PrintActorScheduler

object PriceUpdateCommand {
  def doIt() = {
    val sch = PrintActorScheduler.newScheduler()
    EditionDAO.findAll(10000).foreach(e => {
      PriceUpdater.updatePrice(new SSGPriceProvider(e), sch)
    })
    PriceUpdateActionDAO.insert(new PriceUpdateAction(new Date()))
  }
}


trait PriceProvider {
  def getPrice: Set[PriceSnapshot]
}

object PriceUpdater extends Logging {

  def updatePrice(provider: PriceProvider, sch: IScheduler) {
    val actor = new UpdateActor(sch)
    actor.start()
    actor ! provider
  }

  case class UpdateActor(sch: IScheduler) extends Actor {

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

  object PrintActorScheduler  {
     def newScheduler() : IScheduler = {
      logger.debug("create scheduler")
      val s = new ForkJoinScheduler(10, 10, true, false)
      s.start()
      s
    }
  }
}
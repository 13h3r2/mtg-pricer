package mtg.actions

import mtg.persistence.CardDAO
import actors.scheduler.ForkJoinScheduler
import mtg.ssg.SSGPriceProvider
import mtg.persistence.{PriceUpdateActionDAO, EditionDAO}
import java.util.Date
import actors.{IScheduler, Actor}
import mtg.model.{Edition, PriceSnapshot, PriceUpdateAction}
import mtg.actions.PriceUpdater.{UpdateActor, PrintActorScheduler}
import com.weiglewilczek.slf4s.{Logger, Logging}

trait PriceProvider {
  def prices: Set[PriceSnapshot]
}

object PriceUpdater extends Logging {

  def update {
    val updateActor = new UpdateActor
    updateActor.start
    updateActor ! EditionDAO.findAll(100000).toSet
  }

  class UpdateActor extends Actor with Logging {
    var editionsInProcess: Set[Edition] = Set.empty[Edition]

    def act {
      loop { react {
        case editions: Set[Edition] =>
          //todo this should not be a message to actor
          val sch = PrintActorScheduler.newScheduler()
          editionsInProcess ++= editions
          editions.foreach(ed => {
            val actor = new EditionUpdateActor(sch, this)
            actor.start
            actor ! ed
          })

        case finished: Edition =>
          editionsInProcess -= finished
          logger.debug("end " + finished + ". " + editionsInProcess.size + " to go")
          if (editionsInProcess.isEmpty) {
            logger.info("update complete")
            exit
          }
      }
    }}
  }

  class EditionUpdateActor(sch: IScheduler, updater: Actor) extends Actor with Logging {
    override def scheduler = sch

    def act {
      react {
        case ed: Edition =>
          logger.debug("start " + ed)
          try {
            new SSGPriceProvider(ed)
              .prices
              .filter(obj => !CardDAO.savePriceSnapshot(obj))
              .foreach(a => logger.debug("unable to save " + a))
          }
          finally {updater ! ed}
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
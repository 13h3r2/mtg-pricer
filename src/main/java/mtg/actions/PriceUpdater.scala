package mtg.actions

import actors.scheduler.ForkJoinScheduler
import mtg.ssg.SSGPriceProvider
import actors.{IScheduler, Actor}
import mtg.model.{Edition, PriceSnapshot}
import com.weiglewilczek.slf4s.Logging
import mtg.persistence.{DatabaseUtil, CardDAO, Connection, EditionDAO}

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
    var inProcess: Set[Edition] = Set.empty[Edition]

    def act {
      loop {
        react {
          case editions: Set[Edition] =>
            //todo this should not be a message to actor
            val sch = PrintActorScheduler.newScheduler()
            inProcess ++= editions
            editions.foreach(ed => {
              val actor = new EditionUpdateActor(sch, this)
              actor.start
              actor ! ed
            })

          case finished: Edition =>
            inProcess -= finished
            logger.debug("Edition %s finished. %d to go".format(finished, inProcess.size))
            if (inProcess.isEmpty) {
              logger.info("update complete")
              val postActions = new PostUpdateActions
              postActions.start
              postActions ! "go"
              exit
            }
        }
      }
    }
  }

  class PostUpdateActions extends Actor with Connection with Logging {
    def act() {
      react {
        case _ =>
          RebuildMonthPriceChanges.doIt();
          DatabaseUtil.copyDB("mtg", "mtg-backup")
          logger.info("callbacks complete")
      }
    }
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
          finally {
            updater ! ed
          }
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
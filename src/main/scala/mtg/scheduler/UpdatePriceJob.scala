package mtg.scheduler

import org.quartz.{JobExecutionContext, Job}
import mtg.actions.PriceUpdater

class UpdatePriceJob extends Job {
  def execute(context: JobExecutionContext) {
    PriceUpdater.update
  }
}

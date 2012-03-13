package mtg.api

import mtg.persistence.EditionDAO

class MtgRuPriceProcessor {

  var missedEditions : Set[String] = Set()
  
  def updatePrice(record: Record) {
    val edition = EditionDAO.findNameByAlias(record.getEditionName())
    if( edition == null ) {
      missedEditions += record.getEditionName()
    }
  }

  def process(input: String) : Set[String]  = {
    val records = input
      .split("\n")
      .drop(1)
      .map(_.trim().split(",").toList.map(_.trim()))
      .map(new Record(_))

    records.foreach(updatePrice(_))
    missedEditions
  }
}


case class Record(items: List[String]) {
  def getEditionName(): String = items(1)
}

package mtg.api

import mtg.persistence.{CardDAO, EditionDAO}
import mtg.model.CardItem


class MtgRuPriceProcessor {

  var missedEditions: Set[String] = Set()

  def updatePrice(record: Record) {
    val edition = EditionDAO.findNameByAlias(record.getEditionName())
    if (edition == null) {
      missedEditions += record.getEditionName()
    }
    val snapShot = CardDAO.findLastSnapshot(new CardItem(
      record.getCardName(),
      edition,
      "NM/M",
      record.isFoil()
    ))

    if (snapShot.isEmpty)
      println("missing" + record);
    //else
    //println("get! " + record);

  }

  def parseString(toParse: String) = {
    var result = List[String]()
    var current = ""
    var quotes = false
    toParse.foreach(walker => {
      if(walker == ',' && !quotes) {
        result :+= current.trim()
        current = ""
        quotes = false
      }
      else if(walker == '\"') {
        quotes = !quotes
      }
      else {
        current += walker
      } 
    });
    result
  }

  def process(input: String): Set[String] = {
    val records = input
      .split("\n")
      .drop(1)
      .map(parseString(_))
      .map(new Record(_))

    records.foreach(updatePrice(_))
    missedEditions
  }
}


case class Record(items: List[String]) {
  def getEditionName(): String = items(1)

  def getCardName(): String = items(2).replace("/", "|")

  def isFoil(): Boolean = items(3).contains("foil")
}

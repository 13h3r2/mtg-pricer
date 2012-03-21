package mtg.api

import mtg.persistence.{CardDAO, EditionDAO}
import mtg.model.CardItem
import collection.mutable.MutableList
import scala.math._


object MtgRuPriceProcessor {

  private def updatePrice(record: Record) {
    val edition = EditionDAO.findNameByAlias(record.getEditionName)
    val snapshot = CardDAO.findLastSnapshot(new CardItem(
      record.getCardName,
      edition,
      "NM/M",
      record.isFoil
    ))

    if (snapshot.isEmpty) {
      record.setPrice("PROBLEM")
    }
    else {
      val price = round(snapshot.get.price * 30) - 1
      record.setPrice(price.toString)
    }
  }

  def process(input: String): List[String] = {
    val lines = input.split("\n")
    val header = lines.head
    val records = lines
      .drop(1)
      .map(new Record(_))

    records.foreach(updatePrice(_))
    header :: records.map(_.toCSV).toList
  }
}


class Record() {
  private var items: MutableList[String] = new MutableList[String]()

  def this(line: String) {
    this()
    this.items ++= parseString(line)
  }

  def parseString(toParse: String) = {
    var result = List[String]()
    var current = ""
    var quotes = false
    toParse.foreach(walker => {
      if (walker == ',' && !quotes) {
        result :+= current.trim()
        current = ""
        quotes = false
      }
      else if (walker == '\"') {
        quotes = !quotes
      }
      else {
        current += walker
      }
    })
    result :+= current.trim()
    result
  }

  def getEditionName: String = items(1)

  def getCardName: String = items(2).replace("/", "|")

  def isFoil: Boolean = items(3).contains("foil")

  def setPrice(price: String) = {
    items(5) = price
  }

  def toCSV() = {
    items
      .map(walker => {
        var q = ""
        if (walker.contains(",")) q = "\""
        q + walker + q})
      .mkString(",")
  }
}

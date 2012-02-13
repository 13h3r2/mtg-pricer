package mtg.model

import java.util.Date


case class CardItem(name: String, edition: String, condition: String)

case class PriceSnapshot(item : CardItem, price: Double, date: Date) {
  var diff: Double = 0;

  def this(item : CardItem, price: Double, date: Date, diff: Double) {
    this(item, price, date)
    this.diff = diff
  }
}

case class Card(name: String)

case class Edition(name: String, ssgId: String)


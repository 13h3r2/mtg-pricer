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

case class Edition(name: String, ssgId: String) {
  var alias: List[String] = Nil

  def this(name: String, ssgId: String, alias: List[String]) {
    this(name, ssgId);
    this.alias = alias ::: Nil;
  }

  override def toString = List(name, ssgId, alias).foldLeft("")((a, b)=> (a+","+b))
}


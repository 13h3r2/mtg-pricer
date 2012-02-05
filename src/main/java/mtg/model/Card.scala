package mtg.model

import java.util.Date
import com.osinka.subset._
import com.osinka.subset.Mutation._
import com.weiglewilczek.slf4s.Logging
import com.mongodb.{BasicDBObject, DBObject}


case class CardItem(name: String, edition: String, condition: String)

case class PriceSnapshot(price: Double,date: Date) {
  var diff : Double = 0;
  def this(price: Double, date: Date, diff : Double) {
    this (price, date)
    this.diff = diff
  }
}
case class CardPrice(item: CardItem, prices: List[PriceSnapshot])
case class Card(name: String)
case class Edition(name: String, ssgId: String)


package mtg.model

import com.mongodb.casbah.commons.MongoDBObject

case class Card(name: String)
case class Edition(name: String, ssgId: String)
case class CardItem(card: Card, edition: Edition, condition: String)
case class CardPrice(cardItem : CardItem,  price: Double, count : Int )





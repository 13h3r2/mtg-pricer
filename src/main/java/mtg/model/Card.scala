package mtg.model

import com.mongodb.casbah.commons.MongoDBObject

case class Card(name: String)
case class Edition(name: String, ssgId: String)
case class CardPrice(card : Card,  edition: Edition, condition: String, price : Double )





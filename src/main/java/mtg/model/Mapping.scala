package mtg.model

import com.osinka.subset._
import com.osinka.subset.ValueWriter._
import com.mongodb.DBObject
import com.osinka.subset.ValueReader._
import com.weiglewilczek.slf4s.Logging
import java.util.Date



package object mapping {

  object EditionMapping {
    val name = "name".fieldOf[String]
    val ssgId = "ssgId".fieldOf[String]

  }
  implicit val edWriter = ValueWriter[Edition](edWriterF _)
  def edWriterF(obj: Edition): DBObject = ((EditionMapping.name -> obj.name) ~ (EditionMapping.ssgId -> obj.ssgId))

  object CardItemMapping {
    val name = "name".fieldOf[String]
    val edition = "edition".fieldOf[String]
    val condition = "condition".fieldOf[String]
  }

  implicit val ciWriter = ValueWriter[CardItem](ciWriterF _)
  implicit def ciWriterF(obj: CardItem): DBObject =
    (CardItemMapping.name -> obj.name) ~ (CardItemMapping.edition -> obj.edition) ~ (CardItemMapping.condition -> obj.condition)
  implicit var ciReader = ValueReader[CardItem]({
    case CardItemMapping.name(name) ~ CardItemMapping.edition(edition) ~ CardItemMapping.condition(condition)
    => new CardItem(name, edition, condition)
  })

  object PriceSnapShotMapping extends Logging {
    import CardItemMapping._
    val price = "price".fieldOf[Double]
    val date = "date".fieldOf[Date]
    val diff = "diff".fieldOf[Double]
  }
  implicit val psWriter = ValueWriter[PriceSnapshot](psWriterF _)
  implicit def psWriterF(obj: PriceSnapshot) : DBObject =
    (PriceSnapShotMapping.price -> obj.price) ~ (PriceSnapShotMapping.date -> obj.date) ~ (PriceSnapShotMapping.diff -> obj.diff)
  implicit var psReader = ValueReader[PriceSnapshot]({
    case (PriceSnapShotMapping.price(p) ~ PriceSnapShotMapping.diff(d) ~ PriceSnapShotMapping.date(da))
    => new PriceSnapshot(p, da, d)
  })

  object CardPriceMapping extends Logging{
    import PriceSnapShotMapping._
    import CardItemMapping._
    val item = "item".fieldOf[CardItem]
    val price = "price".subset(PriceSnapshot).of[List[PriceSnapshot]]
  }
  implicit val cpWriter = ValueWriter[CardPrice](cpWriteFunction _)
  implicit def cpWriteFunction(obj: CardPrice): DBObject =
    (CardPriceMapping.item -> obj.item) ~ (CardPriceMapping.price -> obj.prices)
  implicit var cpReader = ValueReader[CardPrice](
    {case CardPriceMapping.price(price) ~ CardPriceMapping.item(item) => new CardPrice(item, price)}
  )
}
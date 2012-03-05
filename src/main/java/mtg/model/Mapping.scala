package mtg.model

import scala.math._
import com.osinka.subset._
import com.mongodb.DBObject
import com.weiglewilczek.slf4s.Logging
import java.util.Date
import scala.Option._
import scala.Option


package object mapping {

  object EditionMapping {
    val name = "name".fieldOf[String]
    val ssgId = "ssgId".fieldOf[String]
    val alias = "alias".fieldOf[List[String]]
  }

  implicit val edWriter = ValueWriter[Edition](edWriterF _)

  def edWriterF(obj: Edition): DBObject = ((EditionMapping.name -> obj.name) ~ (EditionMapping.ssgId -> obj.ssgId) ~ (EditionMapping.alias -> obj.alias))
  implicit var edReader = ValueReader[Edition]({
    case (EditionMapping.name(p) ~ EditionMapping.ssgId(id) ~ EditionMapping.alias(a))
    => { new Edition(p, id, a) };
  })

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
    val item = "item".fieldOf[CardItem]
    val price = "price".fieldOf[Double]
    val date = "date".fieldOf[Date]
    val diff = "diff".fieldOf[Double]
    val absDiff = "absDiff".fieldOf[Double]
  }

  implicit val psWriter = ValueWriter[PriceSnapshot](psWriterF _)

  implicit def psWriterF(obj: PriceSnapshot): DBObject =
    (PriceSnapShotMapping.price -> obj.price) ~ (PriceSnapShotMapping.date -> obj.date) ~ (PriceSnapShotMapping.diff -> obj.diff) ~ (PriceSnapShotMapping.item -> obj.item) ~ (PriceSnapShotMapping.absDiff -> abs(obj.diff))

  implicit var psReader = ValueReader[PriceSnapshot]({
    case (PriceSnapShotMapping.price(p) ~ PriceSnapShotMapping.diff(d) ~ PriceSnapShotMapping.date(da) ~ PriceSnapShotMapping.item(i))
    => new PriceSnapshot(i, p, da, d)
  })

  object PriceUpdateActionMapping {
    val date = "date".fieldOf[Date]
  }

  implicit val puWritter = ValueWriter[PriceUpdateAction](puWriterFunction _)

  implicit def puWriterFunction(obj: PriceUpdateAction): DBObject = (PriceUpdateActionMapping.date -> obj.date)
}
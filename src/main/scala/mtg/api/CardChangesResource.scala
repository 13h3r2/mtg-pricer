package mtg.api

import mtg.persistence.Connection
import com.weiglewilczek.slf4s.Logging
import org.codehaus.jettison.json.{JSONObject, JSONArray}
import com.osinka.subset._
import javax.ws.rs.{DefaultValue, QueryParam, GET, Path => WSPath}


@WSPath("/price/card-changes")
class CardChangesResource extends Connection with Logging {

  @GET
  def dateSearchCard(
                      @DefaultValue("Polluted Delta") @QueryParam("name") name: String,
                      @DefaultValue("Onslaught") @QueryParam("edition") edition: String,
                      @DefaultValue("NM/M") @QueryParam("condition") condition: String,
                      @DefaultValue("false") @QueryParam("foil") foil: Boolean
                      )
  : JSONObject = {
    val result = withConnection(conn => conn("price2")
      .find(
        "item.name".fieldOf[String] === name &&
        "item.condition".fieldOf[String] === condition &&
        "item.edition".fieldOf[String] === edition &&
        "item.foil".fieldOf[Boolean] === foil)
      //.find(PriceSnapShotMapping.date >= begin < end && PriceSnapShotMapping.absDiff > 0)
      .sort("_id.date".fieldOf[Int](1))
      .map(JSONTransformer.transform(_))
      .foldLeft(new JSONArray)((array, item) => array.put(item)))
    new JSONObject().put("result", result)
  }
}

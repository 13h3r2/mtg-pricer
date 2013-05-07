package mtg.api

import mtg.persistence.Connection
import com.weiglewilczek.slf4s.Logging
import org.codehaus.jettison.json.{JSONObject, JSONArray}
import com.osinka.subset._
import javax.ws.rs.{DefaultValue, QueryParam, GET, Path => WSPath}


@WSPath("/price/edition-changes")
class EditionChangesResource extends Connection with Logging {

  @GET
  def dateSearchCard(
                      @DefaultValue("Return to Ravnica") @QueryParam("edition") edition: String,
                      @DefaultValue("false") @QueryParam("foil") foil: Boolean
                      )
  : JSONObject = {
    val result = withConnection(conn => conn("price2Edition")
      .find("_id.edition".fieldOf[String] === edition && "_id.foil".fieldOf[Boolean] === foil)
      //.find(PriceSnapShotMapping.date >= begin < end && PriceSnapShotMapping.absDiff > 0)
      .sort("_id.date".fieldOf[Int](1))
      .map(JSONTransformer.transform(_))
      .foldLeft(new JSONArray)((array, item) => array.put(item)))
    new JSONObject().put("result", result)
  }
}

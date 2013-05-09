package mtg.api

import mtg.persistence.Connection
import com.weiglewilczek.slf4s.Logging
import org.codehaus.jettison.json.{JSONObject, JSONArray}
import com.osinka.subset._
import javax.ws.rs.{QueryParam, DefaultValue, Produces, GET, Path => WSPath}


@WSPath("/cards")
class CardsResource extends Connection with Logging {

  @GET
  def dateSearchCard(
                      @DefaultValue("") @QueryParam("name") name: String,
                      @DefaultValue("0") @QueryParam("offset") offset: Int,
                      @DefaultValue("20") @QueryParam("size") size: Int)
  : JSONObject = {
    if (name.isEmpty) return new JSONObject().put("result", new JSONArray())

    val result = withConnection(conn => conn("cards")
      .find("_id.name".fieldOf[String] === ("^"+name).r)
      .sort("_id.name".fieldOf[Double](1) ~ "_id.edition".fieldOf[Double](1))
      .limit(size)
      .skip(offset)
      .map(JSONTransformer.transform(_))
      .foldLeft(new JSONArray)((array, item) => array.put(item)))
    new JSONObject().put("result", result)
  }


  @GET
  @WSPath("/size")
  def dateSearchCardSize(@DefaultValue("") @QueryParam("name") name: String)
  : JSONObject = {
    if (name.isEmpty) return new JSONObject().put("result", new JSONArray())

    val result = withConnection(conn => conn("cards")
      .count("_id.name".fieldOf[String] === ("^"+name).r))
    new JSONObject().put("result", result)
  }

}

package mtg.rest

import mtg.persistence.Connection
import javax.ws.rs.{DefaultValue, QueryParam, GET, Path}
import com.weiglewilczek.slf4s.Logging
import org.codehaus.jettison.json.{JSONObject, JSONArray}
import java.util.{List => JList}
import com.osinka.subset._


@Path("/price/lastChanges")
class LastChangesResource extends Connection with Logging {

  private def max_size(): Int = 50;

  @GET
  def searchCard(
      @DefaultValue("0") @QueryParam("offset") offset: Int,
      @DefaultValue("20") @QueryParam("size") size: Int)
  : JSONObject = {
    val maxSize = if (size > max_size()) max_size() else size;

    val result = conn("mrPriceLastChange")
      .find()
      .sort("value.maxAbsChange".fieldOf[Double](-1))
      .limit(maxSize)
      .skip(offset)
      .map(JSONTransformer.transform(_))
      .foldLeft(new JSONArray)((array, item) => array.put(item))
    new JSONObject().put("result", result)
  }

}

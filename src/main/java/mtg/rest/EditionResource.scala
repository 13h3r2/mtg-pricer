package mtg.rest

import mtg.persistence.Connection
import javax.ws.rs.{DefaultValue, QueryParam, GET, Path}
import com.weiglewilczek.slf4s.Logging
import org.codehaus.jettison.json.{JSONObject, JSONArray}
import com.osinka.subset._
import mtg.model.mapping.PriceSnapShotMapping
import org.apache.commons.lang.time.DateUtils
import java.util.{Calendar, Date, List => JList}
import java.text.SimpleDateFormat


@Path("/price/edition")
class EditionResource extends Connection with Logging {

  private def max_size(): Int = 50;

  @GET
  def searchCard()
  : JSONObject = {
    val result = conn("edition")
      .find()
      .sort("name".fieldOf[Double](1))
      .map(JSONTransformer.transform(_))
      .foldLeft(new JSONArray)((array, item) => array.put(item))
    new JSONObject().put("result", result)
  }
}

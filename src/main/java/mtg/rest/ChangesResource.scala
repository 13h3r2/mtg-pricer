package mtg.rest

import mtg.persistence.Connection
import javax.ws.rs.{DefaultValue, QueryParam, GET, Path}
import com.weiglewilczek.slf4s.Logging
import org.codehaus.jettison.json.{JSONObject, JSONArray}
import com.osinka.subset._
import mtg.model.mapping.PriceSnapShotMapping
import org.apache.commons.lang.time.DateUtils
import java.util.{Calendar, Date, List => JList}


@Path("/price/lastChanges")
class ChangesResource extends Connection with Logging {

  private def max_size(): Int = 50;

  @GET
  def searchCard(
      @DefaultValue("") @QueryParam("dateStart") dateStart: String,
      @DefaultValue("") @QueryParam("dateEnd") dateEnd: String,
      @DefaultValue("0") @QueryParam("offset") offset: Int,
      @DefaultValue("20") @QueryParam("size") size: Int
                  )
  : JSONObject = {
    val maxSize = if (size > max_size()) max_size() else size;


    val today = new Date()
    val begin = DateUtils.truncate(today, Calendar.DATE)
    val end = DateUtils.truncate(DateUtils.addDays(today, 1), Calendar.DATE)

    val result = conn("price2")
      .find(PriceSnapShotMapping.date >= begin&& PriceSnapShotMapping.date < end && PriceSnapShotMapping.absDiff > 0)
      .sort("absDiff".fieldOf[Double](-1))
      .limit(maxSize)
      .skip(offset)
      .map(JSONTransformer.transform(_))
      .foldLeft(new JSONArray)((array, item) => array.put(item))
    new JSONObject().put("result", result)
  }

}

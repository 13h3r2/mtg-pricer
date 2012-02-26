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


@Path("/price/lastChanges")
class ChangesResource extends Connection with Logging {

  private def max_size(): Int = 50;

  @GET
  def searchCard(
    @DefaultValue("") @QueryParam("date") dateStart: String,
    @DefaultValue("0") @QueryParam("offset") offset: Int,
    @DefaultValue("20") @QueryParam("size") size: Int)
  : JSONObject = {
    val maxSize = if (size > max_size()) max_size() else size;

    val today = new Date()
    var begin = DateUtils.truncate(today, Calendar.DATE)
    if (!dateStart.isEmpty) {
      begin = new SimpleDateFormat("yyyy-MM-dd").parse(dateStart);
    }

    val end = DateUtils.truncate(DateUtils.addDays(begin, 1), Calendar.DATE)

    val result = conn("price2")
      .find(PriceSnapShotMapping.date >= begin < end && PriceSnapShotMapping.absDiff > 0)
      .sort("absDiff".fieldOf[Double](-1) ~ "item.name".fieldOf[Double](1) ~ "item.edition".fieldOf[Double](1))
      .limit(maxSize)
      .skip(offset)
      .map(JSONTransformer.transform(_))
      .foldLeft(new JSONArray)((array, item) => array.put(item))
    new JSONObject().put("result", result)
  }

  @GET
  @Path("/size")
  def searchCardSize(@DefaultValue("") @QueryParam("date") dateStart: String)
  : JSONObject = {
    val today = new Date()
    var begin = DateUtils.truncate(today, Calendar.DATE)
    if (!dateStart.isEmpty) {
      begin = new SimpleDateFormat("yyyy-MM-dd").parse(dateStart);
    }

    val end = DateUtils.truncate(DateUtils.addDays(begin, 1), Calendar.DATE)

    val result = conn("price2")
      .count(PriceSnapShotMapping.date >= begin < end && PriceSnapShotMapping.absDiff > 0)
    new JSONObject().put("result", result)
  }

}

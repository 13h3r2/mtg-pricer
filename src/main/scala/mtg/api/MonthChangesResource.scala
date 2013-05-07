package mtg.api

import mtg.persistence.Connection
import com.weiglewilczek.slf4s.Logging
import org.codehaus.jettison.json.{JSONObject, JSONArray}
import com.osinka.subset._
import mtg.model.mapping.PriceSnapShotMapping
import org.apache.commons.lang.time.DateUtils
import java.util.{Calendar, Date}
import java.text.SimpleDateFormat
import javax.ws.rs.{QueryParam, DefaultValue, Produces, GET, Path => WSPath}


@WSPath("/price/changes/month")
class MonthChangesResource extends Connection with Logging {

  private def max_size = 50;

  @GET
  @Produces
  def dateSearchCard(
                      @DefaultValue("") @QueryParam("date") date: String,
                      @DefaultValue("0") @QueryParam("offset") offset: Int,
                      @DefaultValue("20") @QueryParam("size") size: Int)
  : JSONObject = {
    val maxSize = if (size > max_size) max_size else size

    var begin = new Date
    if (!date.isEmpty) {
      begin = new SimpleDateFormat("yyyy-MM-dd").parse(date)
    }
    begin = DateUtils.truncate(begin, Calendar.MONTH)


    val result = withConnection(conn => conn("priceMonth2")
      .find(PriceSnapShotMapping.date === begin && PriceSnapShotMapping.absDiff > 0)
      .sort("absDiff".fieldOf[Double](-1) ~ "item.name".fieldOf[Double](1) ~ "item.edition".fieldOf[Double](1))
      .limit(maxSize)
      .skip(offset)
      .map(JSONTransformer.transform(_))
      .foldLeft(new JSONArray)((array, item) => array.put(item)))
    new JSONObject().put("result", result)
  }


  @GET
  @WSPath("/size")
  @Produces
  def dateSearchCardSize(@DefaultValue("") @QueryParam("date") date: String)
  : JSONObject = {

    var begin = new Date
    if (!date.isEmpty) {
      begin = new SimpleDateFormat("yyyy-MM-dd").parse(date)
    }
    begin = DateUtils.truncate(begin, Calendar.MONTH)

    val result = withConnection(conn => conn("priceMonth2")
      .count(PriceSnapShotMapping.date === begin && PriceSnapShotMapping.absDiff > 0))
    new JSONObject().put("result", result)
  }

}

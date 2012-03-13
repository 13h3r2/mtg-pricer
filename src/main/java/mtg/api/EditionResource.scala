package mtg.api

import mtg.persistence.Connection
import com.weiglewilczek.slf4s.Logging
import org.codehaus.jettison.json.{JSONObject, JSONArray}
import com.osinka.subset.stringToField
import mtg.model.mapping.PriceSnapShotMapping
import org.apache.commons.lang.time.DateUtils
import java.util.{Calendar, Date, List => JList}
import java.text.SimpleDateFormat
import javax.ws.rs._
import mtg.model.mapping._;
import javax.xml.ws.RequestWrapper


@Path("/edition")
class EditionResource extends Connection with Logging {

  private def max_size(): Int = 50;

  @GET
  @Path("/update")
  def update(
    @QueryParam("name") editionName : String,
    @QueryParam("alias") @DefaultValue("") aliases : String) : JSONObject = {

    val newAliases = aliases
      .split(",")
      .map(_.trim())
      .filter( !_.isEmpty )
      .toList

    conn("edition").update(
      EditionMapping.name(editionName),
      EditionMapping.alias.set(newAliases))

    new JSONObject()
  }

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

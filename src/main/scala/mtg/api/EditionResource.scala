package mtg.api

import mtg.persistence.Connection
import com.weiglewilczek.slf4s.Logging
import org.codehaus.jettison.json.{JSONObject, JSONArray}
import mtg.model.mapping._
import javax.ws.rs.{DefaultValue, QueryParam, GET, Path}
import com.osinka.subset._


@Path("/edition")
class EditionResource extends Connection with Logging {

  private def max_size(): Int = 50

  @GET
  @Path("/update")
  def update(
              @QueryParam("name") editionName: String,
              @QueryParam("alias") @DefaultValue("") aliases: String)
  : JSONObject = {

    val newAliases = aliases
      .split(",")
      .map(_.trim())
      .filter(!_.isEmpty)
      .toList

    withConnection(conn => conn("edition").update(
      EditionMapping.name(editionName),
      EditionMapping.alias.set(newAliases)))

    new JSONObject()
  }

  @GET
  def search(
              @DefaultValue("0") @QueryParam("offset") offset: Int,
             @DefaultValue("20") @QueryParam("size") size: Int)
  : JSONObject = {
    val result = withConnection(conn => conn("edition")
      .find()
      .limit(size)
      .skip(offset)
      .sort("ssgId".fieldOf[Double](-1))
      .map(JSONTransformer.transform(_))
      .foldLeft(new JSONArray)((array, item) => array.put(item)))
    new JSONObject().put("result", result)
  }

  @GET
  @Path("/size")
  def dateSearchCardSize(): JSONObject = {
    new JSONObject().put("result", withConnection(conn => conn("edition").count))
  }
}

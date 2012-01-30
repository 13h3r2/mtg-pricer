package mtg.rest

import javax.ws.rs.{QueryParam, GET, Path}
import mtg.persistance.Connection
import com.mongodb.casbah.commons.MongoDBObject
import collection.JavaConversions
import org.codehaus.jettison.json.{JSONArray, JSONObject}


@Path("/price")
class CardResource extends Connection {

  private val max_size : Int = 50;

  @GET
  def searchCard(
    @QueryParam("name") name: String,
    @QueryParam("offset") offset: Int,
    @QueryParam("offset") size: Int
  ) = {
    val maxSize = if(size > 50)  50 else size;
    val builder = MongoDBObject.newBuilder
    if (name != null) builder += "card_name" -> name

    connection("price")
      .find(builder.result)
      .limit(maxSize)
      .skip(offset)
      .map(obj => {
        new JavaConversions.JSetWrapper(obj.keySet())
          .foldLeft(new JSONObject)((array, item) => array.put(item, obj.get(item)))
      })
      .foldLeft(new JSONArray)((array, item) => array.put(item))
  }
}

object JSONSerializer {
  def serialize(input: AnyVal) {

  }
}
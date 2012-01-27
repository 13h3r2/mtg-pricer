package mtg.rest

import javax.ws.rs.{QueryParam, GET, Path}
import mtg.persistance.Connection
import com.mongodb.casbah.commons.MongoDBObject
import org.codehaus.jettison.json.{JSONObject, JSONArray}
import collection.JavaConversions


@Path("/price")
class CardResource extends Connection {
  @GET
  def searchCard(@QueryParam("name") name: String) = {
    val builder = MongoDBObject.newBuilder
    if (name != null) builder += "card_name" -> name


    val result: JSONArray = new JSONArray()
    connection("price")
    .find(builder.result)
    .map( obj => {
      val result = new JSONObject
      for(x <- new JavaConversions.JSetWrapper(obj.keySet())) result.put(x, obj.get(x))
      result
    })
    .foreach(result.put(_))
    result
  }
}

object JSONSerializer {
  def serialize(input : AnyVal) {

  }
}
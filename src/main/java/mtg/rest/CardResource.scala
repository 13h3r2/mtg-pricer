package mtg.rest

import mtg.persistance.Connection
import com.mongodb.casbah.commons.MongoDBObject
import collection.JavaConversions
import javax.ws.rs.{DefaultValue, QueryParam, GET, Path}
import com.weiglewilczek.slf4s.Logging
import com.mongodb.{DBObject, BasicDBList}
import org.codehaus.jettison.json.{JSONObject, JSONArray}
import java.util.{List => JList}


@Path("/price")
class CardResource extends Connection with Logging{

  private def max_size() : Int = 50;

  @GET
  def searchCard(
    @QueryParam("name") name: String,
    @DefaultValue("0") @QueryParam("offset") offset: Int,
    @DefaultValue("10") @QueryParam("size") size: Int
  ) : JSONObject = {
    val maxSize = if (size > max_size()) max_size() else size;

    val builder = MongoDBObject.newBuilder
    if (name != null) builder += "card_name" -> name

    val result = conn("price")
      .find(builder.result())
      .limit(maxSize)
      .skip(offset)
      .map(transform(_))
      .foldLeft(new JSONArray)((array, item) => array.put(item))
    new JSONObject().put("result", result)
  }
  
  def transform(o : AnyRef ) : AnyRef = {
    o match {
      case list : JList[AnyRef] =>
        JavaConversions.JListWrapper(list).foldLeft(new JSONArray)((array, item)=>array.put(transform(item)))
      case set : DBObject =>
        JavaConversions.JSetWrapper(set.keySet()).foldLeft(new JSONObject)((obj, item)=>obj.put(item, transform(set.get(item))))
      case other => other
    }
  }
}

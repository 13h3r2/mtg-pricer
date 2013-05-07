package mtg.api

import collection.JavaConversions
import com.mongodb.DBObject
import org.codehaus.jettison.json.{JSONObject, JSONArray}
import java.util.{List => JList, Date}
import java.text.SimpleDateFormat

object JSONTransformer {
  val formatter = new SimpleDateFormat("yyyy/MM/dd")

  def transform(o: AnyRef): AnyRef = {
    o match {
      case list: JList[AnyRef] =>
        JavaConversions.JListWrapper(list).foldLeft(new JSONArray)((array, item) => array.put(transform(item)))
      case set: DBObject =>
        JavaConversions.JSetWrapper(set.keySet()).foldLeft(new JSONObject)((obj, item) => obj.put(item, transform(set.get(item))))
      case date: Date => formatter.format(date)
      case other => other
    }
  }
}

package mtg.actions

import mtg.persistence.Connection
import com.mongodb.casbah.map_reduce.MapReduceCommand

object RebuildMonthPriceChanges extends Connection {
  def doIt() = {
    //val mr = MapReduceCommand()
    //mr.map =
      """
    function (key, values) {
        var result = new Object();
        result.item = values[0].item;
        result.price = values[0].price;
        result.date = values[0].date;
        result.diff = 0;
        result.absDiff = 0;
        result.changesCount = 0;
        values.forEach(function (walker) {
            result.diff += walker.diff;
            result.absDiff += walker.absDiff;
            result.changesCount++;
            if (result.date < walker.date) {
                result.date = walker.date;
                result.price = walker.price;
            }
        });
        return result;
    }
    """


    //conn.mapReduce(mr)
  }
}

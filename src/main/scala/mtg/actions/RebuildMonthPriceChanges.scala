package mtg.actions

import mtg.persistence.Connection
import com.mongodb.casbah.map_reduce.{MapReduceStandardOutput, MapReduceCommand}
import com.mongodb.CommandResult
import com.weiglewilczek.slf4s.Logging
import com.mongodb.casbah.Imports

object RebuildMonthPriceChanges extends Connection with Logging {

  private def execCommand(mr: Imports.DBObject) {
    val result: CommandResult = withConnection(conn => conn.command(mr))
    assert(result.ok(), result.toString)
  }

  def doIt() = {
    val mr = MapReduceCommand("price2",
      map = """
              |function () {
              |        if (this.diff != 0) {
              |            this.changesCount = 1;
              |            emit({
              |                    "item":this.item,
              |                    "month":new Date(this.date.getFullYear(), this.date.getMonth(), 1)
              |                },
              |                this);
              |        }
              |    }
            """.stripMargin,
      reduce = """
                 |    function (key, values) {
                 |        var result = new Object();
                 |        result.item = values[0].item;
                 |        result.price = values[0].price;
                 |        result.date = values[0].date;
                 |        result.diff = 0;
                 |        result.absDiff = 0;
                 |        result.changesCount = 0;
                 |        values.forEach(function (walker) {
                 |            result.diff += walker.diff;
                 |            result.changesCount++;
                 |            if (result.date < walker.date) {
                 |                result.date = walker.date;
                 |                result.price = walker.price;
                 |            }
                 |        });
                 |        return result;
                 |    }
               """.stripMargin,
      output = MapReduceStandardOutput("priceMonth"),
      finalizeFunction = Some(
        """
          |function(key, item) {
          |    item.absDiff = item.diff > 0 ? item.diff : -item.diff;
          |    return item;
          |}
        """.stripMargin)
    ).toDBObject

    execCommand(mr)
    withConnection(conn => conn.doEval(
      """
        |db.priceMonth2.remove();
        |db.priceMonth.find().forEach(function (item) {
        |    if (item.absDiff != 0) {
        |        var toInsert = new Object();
        |        toInsert._id = item.value._id;
        |        toInsert.item = item.value.item;
        |        toInsert.date = item._id.month;
        |        toInsert.price = item.value.price;
        |        toInsert.diff = item.value.diff;
        |        toInsert.absDiff = item.value.absDiff;
        |        toInsert.changesCount = item.value.changesCount;
        |        db.priceMonth2.insert(toInsert);
        |    }
        |});
      """.stripMargin))

  }
}

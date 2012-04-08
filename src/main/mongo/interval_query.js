m =
    function () {
        if (this.diff != 0) {
            this.changesCount = 1;
            emit({
                    "item":this.item,
                    "month":new Date(this.date.getFullYear(), this.date.getMonth(), 1)
                },
                this);
        }
    }
;
r =
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
            result.changesCount++;
            if (result.date < walker.date) {
                result.date = walker.date;
                result.price = walker.price;
            }
        });
        return result;
    }
;

f =
    function f(item) {
        item.absDiff = item.diff > 0 ? item.diff : -item.diff;
    }
;

db.price2.mapReduce(m, r, {out:{replace:"priceMonth"}}, f);
db.priceMonth2.remove();
db.priceMonth.find().forEach(function (item) {
    if (item.absDiff != 0) {
        var toInsert = new Object();
        toInsert._id = item.value._id;
        toInsert.item = item.value.item;
        toInsert.date = item._id.month;
        toInsert.diif = item.value.diff;
        toInsert.absDiff = item.value.absDiff;
        toInsert.changesCount = item.value.changesCount;
        db.priceMonth2.insert(toInsert);
    }
});
db.priceMonth2.find().sort({"changesCount":-1});


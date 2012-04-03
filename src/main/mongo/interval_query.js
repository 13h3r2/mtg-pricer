m = function () {
    if(this.diff != 0) {
        this.changesCount = 1;
        emit({
                "item" : this.item,
                "month" : new Date(this.date.getFullYear(), this.date.getMonth(), 1)
            },
            this);
    }
};
r = function (key, values) {
    var result = new Object();
    result.item = values[0].item;
    result.price = values[0].price;
    result.date = values[0].date;
    result.diff = 0;
    result.absDiff = 0;
    result.changesCount = 0;
    values.forEach(function(walker) {
        result.diff += walker.diff;
        result.absDiff += walker.absDiff;
        result.changesCount++;
        if(result.date < walker.date) {
            result.date = walker.date;
            result.price = walker.price;
        }
    });
    return result;
};
db.price2.mapReduce(m, r, {out:{replace:"priceMonth"}});
db.priceMonth.find().sort({"value.changesCount" : -1});


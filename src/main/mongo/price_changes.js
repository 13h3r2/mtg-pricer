r = function () {
    if (this.price.length > 0) {
        var lastPrice = this.price[this.price.length - 1];
        if (lastPrice.diff != 0) {
            var absDiff = lastPrice.diff;
            if (lastPrice.diff < 0) {
                absDiff = -lastPrice.diff
            }
            emit(this.item.name, {
                "info":[
                    { "item":this.item, "price":lastPrice }
                ],
                "maxChange":lastPrice.diff,
                "maxAbsChange":absDiff
            });
        }
    }
};
m = function (key, values) {
    var result = {
        "info":[],
        "maxChange":0,
        "maxAbsChange":0 };
    values.forEach(function (value) {
        if (result.maxAbsChange < value.maxAbsChange) {
            result.maxChange = value.maxChange;
            result.maxAbsChange = value.maxAbsChange;
        }
        value.info.forEach(function (x) {
            result.info.push(x)
        });
    });
    return result;
};
db.price.mapReduce(r, m, {out:{replace:"mrPriceLastChange"}});

db.mrPriceLastChange.find({},{"value.info":0}).sort({"value.maxAbsChange":-1})
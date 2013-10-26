function buildCardsList() {
    m = function () {
        if(condition == "NM/M") {
            emit({
                edition: this.item.edition,
                name: this.item.name, 
                foil: this.item.foil
            }, {
                date: this.date,
                price: this.price
            });
        }
    };
    r = function (key, values) {
        var result = values[0];
        values.forEach(function(item) {
            if(result.date < item.date) {
                result = item;
            }
        })
        return result;
    };
    db.price2.mapReduce(m, r, {out:{replace:"cards"}});
}

buildCardsList();

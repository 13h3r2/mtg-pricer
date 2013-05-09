m = function () {
    if(this.item.condition != "NM/M") {
        return;
    }
    var result = new Object();
    result.diff = this.diff;
    result.changesCount = 1;
    result.latest = false;
    if (this.diff != 0) {
        result.price = 0;
        emit({
            "edition":this.item.edition,
            "foil":this.item.foil,
            "date":new Date(this.date.getFullYear(), this.date.getMonth(), this.date.getDate())
        },
        result);
    } else {
        result.price = this.price;
        emit({
            "edition":this.item.edition,
            "foil":this.item.foil,
            "date":new Date(this.date.getFullYear(), this.date.getMonth(), 1)

        },
        result);
    }
};
r = function (key, values) {
    var result = new Object();
    result.diff = 0;
    result.changesCount = 0;
    result.price = 0;
    result.latest = false;
    values.forEach(function (walker) {
        result.diff += walker.diff;
        result.price += walker.price;
        result.changesCount++;
    });
    return result;
};
f = function(key, item) {
    item.absDiff = item.diff > 0 ? item.diff : -item.diff;
    return item;
}
db.price2.mapReduce(m, r, {out:{replace:"price2Edition"},finalize:f});

currentPrices = {};

db.price2Edition.find().sort({"_id.date":1}).forEach(function(change) {
    var id = change._id.edition+change._id.foil;
    if(currentPrices[id] == null) {
        if(change.value.price != null) {
            currentPrices[id] = change["value"].price;
        }
    } else {
        currentPrices[id] += change["value"].diff;
        change.value.price = currentPrices[id];
        db.price2Edition.update({"_id":change._id}, {"value":change.value}); 
    }
});


db.edition.find().forEach(function(ed) {
    var result = db.price2Edition.find({"_id.edition":ed.name, "_id.foil":true}).sort({"_id.date":-1});
    if(result.hasNext()) {
        id = result.next()._id;
        db.price2Edition.update({_id:id}, {$set:{"value.latest":true}});
    }
    var result = db.price2Edition.find({"_id.edition":ed.name, "_id.foil":false}).sort({"_id.date":-1});
    if(result.hasNext()) {
        id = result.next()._id;
        db.price2Edition.update({_id:id}, {$set:{"value.latest":true}});
    }
});

db.price2Edition.find({"value.latest":true, "_id.foil":false}).sort({"value.price":-1});


rs = db.price2Edition.find({"_id.edition":"2013 Core Set", "_id.foil":false}).sort({"_id.date":1});
    




db.price.find().forEach(function (p) {
    p.price[0]["item"] = p.item;
    db.price2.insert(p.price[0]);
//    for(i in p.price) {
//        if( i > 1 && p.price[i].diff != 0) {
//            p.price[i]["item"] = p.item;
//            db.priceChanges.insert(p.price[i]);
//        }
//    }
});


db.price2.ensureIndex({item:1});
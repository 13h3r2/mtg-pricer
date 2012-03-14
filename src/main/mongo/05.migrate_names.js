db.price2.find().forEach(function (item) {
        var e = item["item"]["edition"];
        if (e.indexOf("Foil") == -1) {
            var ed = db.edition.findOne({"alias":e});
            if (ed == null)
                print(e + " - " + ed);
            db.price2.update({"_id":item["_id"]}, {$set:{"item.edition":ed.name }});
        }
        else {
            var eWoFoil = e.substring(0, e.indexOf("Foil") - 1).trim();
            var ed = db.edition.findOne({"alias":eWoFoil});
            if (ed == null) {
                print(eWoFoil + " - " + ed);
            }
            db.price2.update({"_id":item["_id"]}, {$set:{"item.edition":ed.name }});
        }
    }
);

//checker for edition presence
db.price2.distinct("item.edition").forEach(function (e) {
        var ed = db.edition.findOne({"name":e});
        if (ed == null)
            print(e + " - " + ed);
    }
);

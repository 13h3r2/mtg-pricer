//checker for edition presence
db.price2.distinct("item.edition").forEach(function (e) {
        if (e.indexOf("Foil") == -1) {
            var ed = db.edition.findOne({"alias":e});
            if( ed == null)
            print(e + " - " + ed);
        }
        else
        {
            var eWoFoil = e.substring(0, e.indexOf("Foil") - 1).trim();
            var ed = db.edition.findOne({"alias":eWoFoil});
            if (ed == null) {
                print(eWoFoil + " - " + ed);
            }
        }
    }
);


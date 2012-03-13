db.price2.find().forEach(function (p) {
    var foil = p["item"]["name"].indexOf("FOIL") != -1;
    if( foil == true) {
        p["item"]["foil"] = foil;
        delete p["foil"]
        db.price2.update({"_id":p["_id"]}, p);
    }
});
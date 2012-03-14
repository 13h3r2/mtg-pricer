db.price2.find().forEach(function (p) {
    var pos = p["item"]["name"].indexOf("FOIL");
    var foil = pos != -1;
    if( foil == true) {
        p["item"]["foil"] = foil;
        delete p["foil"];
        p["item"]["name"] = p["item"]["name"].substring(0, pos - 1).trim();
        db.price2.update({"_id":p["_id"]}, p);
    }
});

db.price2.find().forEach(function (p) {
        if(p["item"]["foil"]
        delete p["foil"];
        p["item"]["name"] = p["item"]["name"].substring(0, pos - 1).trim();
        db.price2.update({"_id":p["_id"]}, p);
    }
});


//checker
db.price2.find({"item.name" : {$regex:".*FOIL.*"}});
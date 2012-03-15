db.price2.find().forEach(function (p) {
    var posName = p["item"]["name"].indexOf("(FOIL)");
    var posEdition = p["item"]["edition"].indexOf("(Foil)");
    var foil = posEdition != -1 || posName != -1;
    var oldItem  = p["item"];

    p["item"]["foil"] = foil;
    if(posName != -1 )
        p["item"]["name"] = p["item"]["name"].substring(0, posName - 1).trim();
    else
        p["item"]["name"] = oldItem["name"];

    if(posEdition != -1 )
        p["item"]["edition"] = p["item"]["edition"].substring(0, posEdition - 1).trim();
    else
        p["item"]["edition"] = oldItem["edition"];
    p["item"]["condition"] = oldItem["condition"];
    db.price2.update({"_id":p["_id"]}, p);
});

//checker
db.price2.find({"item.name":{$regex:".*FOIL.*"}});
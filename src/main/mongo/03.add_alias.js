db.edition.find().forEach(function (p) {
    p["alias"] = [];
    db.edition.update({"_id":p["_id"]}, p);
});

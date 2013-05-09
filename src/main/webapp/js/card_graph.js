function CardGraph(page) {
    var self = this;

    this.name = encodeURI("Polluted Delta");
    this.condition = encodeURI("NM/M");
    this.edition = encodeURI("Onslaught");
    this.foil = encodeURI("false");


    this.g = new Dygraph(
        document.getElementById("card-graph"),
        "Date,Temperature\n" +
            "2008-05-07,75\n" +
            "2008-05-09,80\n",
        {"includeZero": true, "width": 900, "height": 450, "legend": "always"}
    );

    this.load = function () {
        _ajaxCall("api/price/card-changes?edition=" + self.edition + "&condition=" + self.condition + "&name=" + self.name + "&foil=" + self.foil,
            function(json) {
                var newData = [];
                console.log(json);
                json["result"].forEach(function(elem) {
                    newData.push([new Date(elem["date"]), elem["price"]])
                });
                self.g.updateOptions({"file": newData, labels : ["Time", decodeURI(self.name)]});
            }
        );
    }
}

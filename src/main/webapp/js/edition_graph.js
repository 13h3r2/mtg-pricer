function EditionGraph(page) {
    var self = this;
    this.g = new Dygraph(
        document.getElementById("edition-graph"),
        "Date,Temperature\n" +
            "2008-05-07,75\n" +
            "2008-05-09,80\n",
        {"includeZero": true, "width": 900, "height": 450}
    );

    this.availableEditions = ko.observableArray();
    this.selectedEdition = ko.observable("");
    this.selectedEdition.subscribe(function() {self.load()});
    this.foil = ko.observable(false);
    this.foil.subscribe(function() {self.load()});

    this.load = function () {
        _ajaxCall("api/price/edition-changes?edition=" + encodeURI(self.selectedEdition()) + "&foil=" + self.foil(),
            function(json) {
                var newData = [];
                json["result"].forEach(function(elem) {
                    newData.push([new Date(elem["_id"]["date"]), elem["value"]["price"]])
                });
                self.g.updateOptions({"file": newData});
            }
        );
    }

    this.loadAvailableEditions = function() {
        _ajaxCall("/api/edition?size=200",
            function (json) {
                self.availableEditions.removeAll();
                json["result"].forEach(function(elem) {
                    self.availableEditions.push(elem["name"]);
                });
            }
        );
    }

    this.loadAvailableEditions();
}

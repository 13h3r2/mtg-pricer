function Edition(name, aliases, editionsList) {
    var self = this;
    this.editionsList = editionsList;
    this.isEditMode = ko.observable(false);
    this.name = name;
    this.aliases = ko.observable(aliases);
    this.aliasesEdit = ko.observable(aliases);
    this.switchEdit = function() {
        if(!this.isEditMode()) {
            this.editionsList.clearAllSelected();
            this.isEditMode(true);
        }
    };
    this.save = function() {
        _ajaxCall("/api/edition/update?" +
            "name=" + encodeURI(this.name) +
            "&alias=" + encodeURI(this.aliasesEdit()),
            function() {
                self.aliases(self.aliasesEdit());
                self.isEditMode(false);
            }
        );
    };
    this.cancel = function() {
        this.aliasesEdit(this.aliases());
        this.isEditMode(false);
    };
}

function Editions(page) {
    var self = this;
    this.page = page;
    this.editions = ko.observableArray([
        new Edition("Test", ["Test", "TST", "TESTTT"])
    ]);
    this.reload = function() {
        _ajaxCall("/api/edition",
            function (json) {
                self.editions.removeAll();
                var result = json["result"];
                for (var i in result) {
                    if (result.hasOwnProperty(i)) {
                        self.editions.push(
                            new Edition(
                                result[i]["name"],
                                result[i]["alias"].join(", "),
                                self
                            ));
                    }
                }
            })
    }

    this.clearAllSelected = function() {
        this.editions().forEach(function(edition) { edition.isEditMode(false)});
    }
}

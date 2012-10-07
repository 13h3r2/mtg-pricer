function Edition(name, ssgId, aliases, editionsList) {
    var self = this;
    this.ssgId = ssgId;
    this.editionsList = editionsList;
    this.isEditMode = ko.observable(false);
    this.name = name;
    this.aliases = ko.observable(aliases);
    this.aliasesEdit = ko.observable(aliases);

    this.switchEdit = function () {
        if (!this.isEditMode()) {
            this.editionsList.clearAllSelected();
            this.isEditMode(true);
        }
    };
    this.save = function () {
        _ajaxCall("/api/edition/update?" +
            "name=" + encodeURI(this.name) +
            "&alias=" + encodeURI(this.aliasesEdit()),
            function () {
                self.aliases(self.aliasesEdit());
                self.isEditMode(false);
            }
        );
    };
    this.cancel = function () {
        this.aliasesEdit(this.aliases());
        this.isEditMode(false);
    };
}
function EditionsProvider(table) {
    var self = this;
    this.loadPage = function () {
        _ajaxCall("/api/edition?size=20" +
            "&offset=" + table.currentPage(),
            function (json) {
                table.items.removeAll();
                var result = json["result"];
                var newItems = [];
                for (var i in result) {
                    if (result.hasOwnProperty(i)) {
                        newItems.push(
                            new Edition(
                                result[i]["name"],
                                result[i]["ssgId"],
                                result[i]["alias"].join(", "),
                                self
                            ));
                    }
                }
                table.items(newItems);
            })
    };

    this.load = function () {
        _ajaxCall("/api/edition/size",
            function (json) {
                table.pages.removeAll();
                var count = json["result"];
                var currentPage = 0;
                do {
                    currentPage++;
                    table.pages.push(new AjaxTableLink("" + currentPage, (currentPage - 1) * table.ITEMS_PER_PAGE));
                } while (currentPage * table.ITEMS_PER_PAGE < count);
                table.currentPage(0);
                self.loadPage();
            });
    }


}

function Editions(page) {
    this.page = page;
    this.table = new AjaxTable(page);
    this.table.provider = new EditionsProvider(this.table)

//    this.clearAllSelected = function() {
//        this.editions().forEach(function(edition) { if(edition.isEditMode()) edition.isEditMode(false)});
//    }
}

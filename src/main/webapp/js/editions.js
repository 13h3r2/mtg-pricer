function Edition(id, name, ssgId, aliases, editions) {
    var self = this;
    this.id = id;
    this.ssgId = ssgId;
    this.editions = editions;
    this.isEditMode = ko.observable(false);
    this.name = name;
    this.aliases = ko.observable(aliases);
    this.aliasesEdit = ko.observable(aliases);

    this.edit = function(element) {
        console.log(element);
    }

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
    };
}
function EditionsProvider(editions) {
    var self = this;
    this.loadPage = function () {
        _ajaxCall("/api/edition?size=20" +
            "&offset=" + editions.table.currentPage(),
            function (json) {
                editions.table.items.removeAll();
                var result = json["result"];
                var newItems = [];
                for (var i in result) {
                    if (result.hasOwnProperty(i)) {
                        newItems.push(
                            new Edition(
                                result[i]["_id"],
                                result[i]["name"],
                                result[i]["ssgId"],
                                result[i]["alias"].join(", "),
                                self
                            ));
                    }
                }
                editions.table.items(newItems);
            })
    };

    this.load = function () {
        _ajaxCall("/api/edition/size",
            function (json) {
                editions.table.pages.removeAll();
                var count = json["result"];
                var currentPage = 0;
                do {
                    currentPage++;
                    editions.table.pages.push(new AjaxTableLink("" + currentPage, (currentPage - 1) * editions.table.ITEMS_PER_PAGE));
                } while (currentPage * editions.table.ITEMS_PER_PAGE < count);
                editions.table.currentPage(0);
                self.loadPage();
            });
    }


}

function EditionEditPanel(editions) {

}

function Editions(page) {
    this.page = page;
    this.editionEditPanel = new EditionEditPanel(this);
    this.table = new AjaxTable(page);
    this.table.provider = new EditionsProvider(this)
}

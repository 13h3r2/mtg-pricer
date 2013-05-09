function CardItem(name, edition, foil, price) {
    var self = this;
    this.name = name;
    this.edition = edition;
    this.price = price;
    this.foil = foil;

    this.fullName = function() {
        return self.name + " (" + self.edition + ")" + (this.foil ? " FOIL " : "");
    }
}

function Cards(page) {
    var self = this;
    this.name = ko.observable("Polluted Delta");
    this.table = new AjaxTable(page);
    this.table.provider = new CardsProvider(this);

    this.search = function() {
        self.table.load();
    }
    this.showGraph = function(item) {
        page.card_graph.name = item.name;
        page.card_graph.edition = item.edition;
        page.card_graph.foil = item.foil;
        page.card_graph.condition = "NM/M";
        page.navigation.activateByName("Card Graphs");
    }
}

function CardsProvider(cards) {
    var self = this;
    this.cards = cards;
    this.loadPage = function () {
        _ajaxCall("/api/cards?size=20&name="+encodeURI(cards.name())+"&offset=" + cards.table.currentPage(),
            function (json) {
                cards.table.items.removeAll();
                json["result"].forEach(function(item) {
                    cards.table.items.push(
                        new CardItem(
                            item["_id"]["name"],
                            item["_id"]["edition"],
                            item["_id"]["foil"],
                            item["value"]["price"]
                        )
                    );
                });
            });
    };

    this.load = function () {
        _ajaxCall("/api/cards/size?name="+encodeURI(cards.name()),
            function (json) {
                cards.table.pages.removeAll();
                var count = json["result"];
                var currentPage = 0;
                do {
                    currentPage++;
                    cards.table.pages.push(new AjaxTableLink("" + currentPage, (currentPage - 1) * cards.table.ITEMS_PER_PAGE));
                } while (currentPage * cards.table.ITEMS_PER_PAGE < count);
                cards.table.currentPage(0);
                self.loadPage();
            }
        );
    }


}

function PriceChange(name, date, diff, edition, condition, foil, current) {
    this.name = name;
    this.edition = edition;
    this.date = date;
    this.condition = condition;
    this.diff = diff;
    this.foil = foil;
    this.current = current;
    this.fullName = function () {
        return this.name + " - " + this.edition + (this.foil ? " FOIL " : "") + " (" + this.condition + ")";
    };
    this.diffWithSign = function () {
        return this.diff > 0 ? "+" + diff : diff;
    };
}


function ChangesPanel(page) {
    var self = this;
    this.table = new AjaxTable(page);
    this.date = ko.observable(new Date());
    this.dayChangeProvider = new DayChangesDataProvider(this, this.table);
    this.table.provider = this.dayChangeProvider;

    this.dateText = ko.dependentObservable(function () {
        return $.datepicker.formatDate('yy-mm-dd', this.date());
    }, this);

    this.next = function () {
        var newDate = new Date(this.date().getTime() + 1000 * 60 * 60 * 24);
        this.date(newDate);
        this.table.load();
    };
    this.prev = function () {
        var newDate = new Date(this.date().getTime() - 1000 * 60 * 60 * 24);
        this.date(newDate);
        this.table.load();
    };
}

function MonthChangesPanel(page) {
    var self = this;
    this.table = new AjaxTable(page);

    var firstDayOfCurrentMonth = new Date();
    firstDayOfCurrentMonth.setDate(1);
    this.date = ko.observable(firstDayOfCurrentMonth);

    this.monthChangeProvider = new MonthChangesDataProvider(this, this.table);
    this.table.provider = this.monthChangeProvider;

    this.dateText = ko.dependentObservable(function () {
        return $.datepicker.formatDate('yy-mm-dd', this.date());
    }, this);

    this.next = function () {
        var newDate = new Date(this.date().getTime());
        newDate.setMonth(newDate.getMonth() + 1);
        this.date(newDate);
        this.table.load();
    };
    this.prev = function () {
        var newDate = new Date(this.date().getTime());
        newDate.setMonth(newDate.getMonth() - 1);
        this.date(newDate);
        this.table.load();
    };
}



function MonthChangesDataProvider(changesPanel, table) {
    var self = this;
    this.loadPage = function () {
        _ajaxCall("/api/price/changes/month?size=20" +
            "&offset=" + table.currentPage() +
            "&date=" + changesPanel.dateText(),
            function (json) {
                table.items.removeAll();
                var result = json["result"];
                var newItems = [];
                for (var i in result) {
                    if (result.hasOwnProperty(i)) {
                        var item = result[i]["item"];
                        newItems.push(
                            new PriceChange(
                                item["name"],
                                item["date"],
                                result[i]["diff"],
                                item["edition"],
                                item["condition"],
                                item["foil"],
                                result[i]["price"]));
                    }
                }
                table.items(newItems);
            })
    };

    this.load = function () {
        _ajaxCall("/api/price/changes/month/size?date=" + changesPanel.dateText(),
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



function DayChangesDataProvider(changesPanel, table) {
    var self = this;
    this.loadPage = function () {
        _ajaxCall("/api/price/changes/date?size=20" +
            "&offset=" + table.currentPage() +
            "&date=" + changesPanel.dateText(),
            function (json) {
                table.items.removeAll();
                var result = json["result"];
                var newItems = [];
                for (var i in result) {
                    if (result.hasOwnProperty(i)) {
                        var item = result[i]["item"];
                        newItems.push(
                            new PriceChange(
                                item["name"],
                                item["date"],
                                result[i]["diff"],
                                item["edition"],
                                item["condition"],
                                item["foil"],
                                result[i]["price"]));
                    }
                }
                table.items(newItems);
            })
    };

    this.load = function () {
        _ajaxCall("/api/price/changes/date/size?date=" + changesPanel.dateText(),
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

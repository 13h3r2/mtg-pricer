function PriceChange(name, diff, edition, condition, current) {
    this.name = name;
    this.edition = edition;
    this.condition = condition;
    this.diff = diff;
    this.current = current;
    this.fullName = function () {
        return this.name + " - " + this.edition + " (" + this.condition + ")";
    };
    this.diffWithSign = function () {
        return this.diff > 0 ? "+" + diff : diff;
    };
}



function TableNavigationLink(name, offset) {
    this.name = name;
    this.offset = offset;
}

function PriceTable(page) {
    var self = this;
    this.currentOffset = ko.observable(0);
    this.pages = ko.observableArray([new TableNavigationLink("1", 0)]);
    this.changes = ko.observableArray([]);
    this.itemsPerPage = 20;

    this.selectPage = function (navigationLink) {
        if (navigationLink.offset != self.currentOffset()) {
            self.currentOffset(navigationLink.offset);
            self.loadData();
        }
    }

    this.reload = function () {
        _ajaxCall("/api/price/lastChanges/size?date=" + page.dayChanges.dateText(),
            function (json) {
                self.pages.removeAll();
                var count = json["result"];
                var currentPage = 0;
                do {
                    currentPage++;
                    self.pages.push(new TableNavigationLink("" + currentPage, (currentPage - 1) * self.itemsPerPage));
                } while (currentPage * self.itemsPerPage < count);
                self.currentOffset(0);
                self.loadData();
            });
    }

    this.loadData = function () {
        _ajaxCall("/api/price/lastChanges?size=20" +
            "&offset=" + self.currentOffset() +
            "&date=" + page.dayChanges.dateText(),
            function (json) {
                self.changes.removeAll();
                var result = json["result"];
                for (var i in result) {
                    if (result.hasOwnProperty(i)) {
                        var item = result[i]["item"];
                        self.changes.push(
                            new PriceChange(
                                item["name"],
                                result[i]["diff"],
                                item["edition"],
                                item["condition"],
                                result[i]["price"]));
                    }
                }
            })
    };
}

function DayChanges(page) {
    var self = this;
    this.table = new PriceTable(page);
    this.date = ko.observable(new Date());
    this.dateText = ko.dependentObservable(function () {
        return $.datepicker.formatDate('yy-mm-dd', this.date());
    }, this);

    this.next = function () {
        var newDate = new Date(this.date().getTime() + 1000 * 60 * 60 * 24);
        this.date(newDate);
        this.table.reload();
    };
    this.prev = function () {
        var newDate = new Date(this.date().getTime() - 1000 * 60 * 60 * 24);
        this.date(newDate);
        this.table.reload();
    };
}

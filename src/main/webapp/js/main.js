$(document).ready(function () {
        var page = new Page();
        ko.applyBindings(page);
    }
);

function PriceChange(name, diff, edition, condition, current) {
    this.name = name;
    this.edition = edition;
    this.condition = condition;
    this.diff = diff;
    this.current = current;
    this.fullName = function () {
        return this.name + " - " + this.edition + " (" + this.condition + ")";
    };
}

function NavigationItem(name, action, style) {
    this.name = name;
    this.action = action;
    this.style = style;
}


function Navigation(page) {
    this.items = ko.observableArray([
        new NavigationItem("Last day changes", page.dayChanges.callChanges, "active")
    ]
    );
}

function DayChanges(page) {
    this.changes = ko.observableArray([]);
    this.date = ko.observable(new Date());
    this.dateText = ko.dependentObservable(function() {return $.datepicker.formatDate('yy-mm-dd', this.date());}, this);

    this.next = function() {
        var newDate = new Date();
        newDate.setDate(this.date().getDate() + 1)
        this.date(newDate);
        this.callChanges();
    };
    this.prev = function() {
        var newDate = new Date();
        newDate.setDate(this.date().getDate() - 1)
        this.date(newDate);
        this.callChanges();
    };

    this.callChanges = function () {
        _ajaxCall("/api/price/lastChanges?size=20&date=" + page.dayChanges.dateText(),
            function (json) {
                page.dayChanges.changes.removeAll();
                var result = json["result"];
                for (var i in result) {
                    var item = result[i]["item"];
                    page.dayChanges.changes.push(
                        new PriceChange(
                            item["name"],
                            result[i]["diff"],
                            item["edition"],
                            item["condition"],
                            result[i]["price"]));
                }
            })
    };
}

function Page() {
    this.me = this;
    this.dayChanges = new DayChanges(this.me);
    this.navigation = new Navigation(this.me);
}

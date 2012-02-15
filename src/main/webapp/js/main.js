$(document).ready(function () {
        var page = new Page();
        ko.applyBindings(page);
    }
);

function PriceChange(name, diff, edition, condition) {
    this.name = name;
    this.edition = edition;
    this.condition = condition;
    this.diff = diff;
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
        new NavigationItem("Last day changes", function () {
                _ajaxCall("/api/price/lastChanges?from=" + page.changesPanel.from() + "&to=" + page.changesPanel.to(),
                    function (json) {
                        page.changesPanel.changes.removeAll();
                        var result = json["result"];
                        for (var i in result) {
                            var item = result[i]["item"];
                            page.changesPanel.changes.push(new PriceChange(item["name"], result[i]["diff"], item["edition"], item["condition"]));
                        }
                    })
            }, "active"
        )]
    );
}

function ChangesPanel(page) {
    this.changes = ko.observableArray([
    ]);
    this.from = ko.observable($.datepicker.formatDate('yy-mm-dd', new Date()));
    this.to = ko.observable($.datepicker.formatDate('yy-mm-dd', new Date()));
}

function Page() {
    this.me = this
    this.changesPanel = new ChangesPanel(this.me)
    this.navigation = new Navigation(this.me);
}


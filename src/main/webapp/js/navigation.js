function Navigation(page) {
    this.items = ko.observableArray([
        new NavigationItem(this, "Last day changes", page.dayChanges.table.reload, page.dayChanges),
        new NavigationItem(this, "Editions", page.editions.reload, page.editions)
    ]
    );
    this.active = ko.observable(this.items()[0].panel);
}

function NavigationItem(navigation, name, activate, panel) {
    var self = this;
    this.navigation = navigation;
    this.name = name;
    this.activate = activate;
    this.panel = panel;

    this.click = function () {
        //set active at navigation
        self.navigation.active(this.panel);
        //read data
        self.activate();
    }
}

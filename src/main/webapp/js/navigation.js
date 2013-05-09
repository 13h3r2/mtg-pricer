function Navigation(page) {
    this.items = ko.observableArray([
        new NavigationItem(this, "Day changes", page.changes.table.load, page.changes),
        new NavigationItem(this, "Month changes", page.monthChanges.table.load, page.monthChanges),
        new NavigationItem(this, "Editions", page.editions.table.load, page.editions),
        new NavigationItem(this, "Cards", null, page.cards),
        new NavigationItem(this, "Card Graphs", page.card_graph.load, page.card_graph),
        new NavigationItem(this, "Edition Graphs", page.edition_graph.load, page.edition_graph),
        new NavigationItem(this, "Export", null, page.upload)
    ]
    );
    this.active = ko.observable(this.items()[0].panel);
    this.activateByName = function(name) {
        this.items().forEach(function(item) {
            if(item.name == name) {
                item.click();
            }
        });
    };
}

function NavigationItem(navigation, name, activate, panel) {
    var self = this;
    this.navigation = navigation;
    this.name = name;
    this.activate = activate;
    this.panel = panel;

    this.click = function () {
        //set active at navigation
        self.navigation.active(self.panel);
        //read data
        if( self.activate != null )
            self.activate();
    }
}

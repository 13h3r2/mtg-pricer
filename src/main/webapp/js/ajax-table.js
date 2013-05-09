function AjaxTable(page) {
    this.ITEMS_PER_PAGE = 20;

    var self = this;
    this.currentPage = ko.observable(0);
    this.pages = ko.observableArray([new AjaxTableLink("1", 0)]);
    this.items = ko.observableArray([]);
    this.provider = null;

    this.selectPage = function (navigationLink) {
        if (navigationLink.offset != self.currentPage()) {
            self.currentPage(navigationLink.offset);
            self.loadPage();
        }
    };

    this.load = function() {
        self.provider.load();
    }
    this.loadPage = function() {
        self.provider.loadPage();
    }
}

function AjaxTableLink(name, offset) {
    this.name = name;
    this.offset = offset;
}


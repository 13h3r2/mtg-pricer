function FoundCard(name) {
    this.name = name
}

function Page() {
    this.me = this
    this.searchText = ko.observable("enter search here");
    this.foundCards = ko.observableArray([
        new FoundCard("Tundra"),
        new FoundCard("Taiga"),
        new FoundCard("Tropical Island")
    ]);


    this.doSearch = function() {
        //this.foundCards([new FoundCard("Click!")]);
        this.foundCards.removeAll();
        this.foundCards.push(new FoundCard("Test"));

    }
}

$(document).ready(function () {
        ko.applyBindings(new Page());
    }
)
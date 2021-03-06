$(document).ready(function () {
        var page = new Page();
        ko.applyBindings(page);
    }
);

function Page() {
    this.changes = new ChangesPanel(this);
    this.monthChanges = new MonthChangesPanel(this);
    this.editions = new Editions(this);
    this.edition_graph = new EditionGraph(this);
    this.card_graph = new CardGraph(this);
    this.cards = new Cards(this);
    this.upload = new UploadPanel(this);
    this.updateInfo = new UpdateInfo(this);

    this.navigation = new Navigation(this);
}


function UpdateInfo() {
    this.update = function () {
        _ajaxCall("/api/price/update",
            function (json) {
        });
    }
}

function UploadPanel() {

}
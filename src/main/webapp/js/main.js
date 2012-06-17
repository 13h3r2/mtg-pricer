$(document).ready(function () {
        var page = new Page();
        ko.applyBindings(page);
    }
);

function Page() {
    this.сhanges = new ChangesPanel(this);
    this.monthChanges = new MonthChangesPanel(this);
    this.editions = new Editions(this);
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
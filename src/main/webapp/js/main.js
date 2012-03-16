$(document).ready(function () {
        var page = new Page();
        ko.applyBindings(page);
    }
);

function Page() {
    this.dayChanges = new DayChanges(this);
    this.editions = new Editions(this);
    this.upload = new UploadPanel(this);
    this.updateInfo = new UpdateInfo(this);
    this.navigation = new Navigation(this);
}


function UpdateInfo() {
    this.update = function () {
        _ajaxCall("/api/price/lastChanges/update",
            function (json) {
        });
    }
}

function UploadPanel() {

}
$(document).ready(function () {
        var page = new Page();
        ko.applyBindings(page);
    }
);

function Page() {
    this.dayChanges = new DayChanges(this);
    this.editions = new Editions(this);
    this.upload = new UploadPanel(this);
    this.navigation = new Navigation(this);
}

function UploadPanel() {

}
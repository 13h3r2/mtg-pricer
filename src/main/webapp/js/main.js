$(document).ready(function () {
        var page = new Page();
        ko.applyBindings(page);
    }
);

function Page() {
    this.dayChanges = new DayChanges(this);
    this.editions = new Editions(this);
    this.navigation = new Navigation(this);

    this.upload = new UploadPanel();
}

function UploadPanel() {
    this.file
}
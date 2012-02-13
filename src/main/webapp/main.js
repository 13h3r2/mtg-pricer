function PriceChange(name, diff) {
    this.name = name;
    this.diff = diff;
}

function NavigationItem(name, action, style) {
    this.name = name;
    this.action = action;
    this.style = style;
}
function Navigation(page) {
    this.items = ko.observableArray([
        new NavigationItem("Last day changes", function () {
                _ajaxCall("/api/price/lastChanges", function (json) {
                    page.changes.removeAll();
                    var result = json["result"];
                    console.log("r" +  result);
                    for(var i in result) {
                        console.log(i);
                        page.changes.push(new PriceChange(result[i]["item"]["name"] + " (" + result[i]["item"]["edition"] + ")", result[i]["diff"]));
                    }
                })
            }, "active"
        )]
    );

}

function Page() {
    this.me = this
    this.changes = ko.observableArray([
        new PriceChange("Tundra", 10),
        new PriceChange("Taiga", -5),
        new PriceChange("Tropical Island", -10)
    ]);

    this.navigation = new Navigation(this.me);
}


$(document).ready(function () {
        var page = new Page();
        ko.applyBindings(page);
    }
);

function _ajaxCall(url, callback, message, noEndCallback) {
    console.log("get " + url);
    var messageText = 'Loading...';
    if (message != null) {
        messageText = message + '...';
    }

    $("#loadingLabel").text(messageText);
    $("#loadingLabel").show();
    get = $.get(url, function () {
    })
        .error(_ajaxCallFail)
        .success(callback)
    ;
    if (noEndCallback == null || noEndCallback == false) {
        get.complete(_ajaxCallEnd);
    }
}
function _ajaxCallFail(error) {
    _notificationError("" + error.status + ": " + error.responseText);
}

function _ajaxCallEnd(json) {
    $("#loadingLabel").hide();
    console.log("got");
    console.log(json);
}



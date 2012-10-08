
function _ajaxCall(url, callback, message, noEndCallback) {
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
}



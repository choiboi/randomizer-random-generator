// This function will open up an external browser app on the BB device.
var openBrowser = function(link) {
    var args = new blackberry.invoke.BrowserArguments(link);
    blackberry.invoke.invoke(blackberry.invoke.APP_BROWSER, args);
}; 
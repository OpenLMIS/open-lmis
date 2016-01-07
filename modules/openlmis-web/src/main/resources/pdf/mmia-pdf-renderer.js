"use strict";
var page = require('webpage').create(),
    system = require('system'),
    common = require('./pdf-common');

var address = system.args[1];
var output = system.args[2];
var sessionId = system.args[3];

common.addCookie(address, sessionId);
common.monitorResponses(page, address, onLoaded);

function onLoaded() {
    console.log("message loaded, prepare to render");
    window.setTimeout(function () {
        page.render(output);
        phantom.exit();
    }, 200);
}
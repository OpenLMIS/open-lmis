"use strict";
var page = require('webpage').create(),
    system = require('system');

var address = system.args[1];
var output = system.args[2];
var sessionId = system.args[3];

phantom.addCookie({
    'name': 'JSESSIONID',
    'value': sessionId,
    'domain': 'localhost'
});

page.open(address, function (status) {

    if (status !== 'success') {
        console.log('Unable to load the address!');
        phantom.exit(1);
    } else {
        window.setTimeout(function () {
            page.evaluate(function () {
                $("#locale_pt").click();
                $(".toggleFullScreen").hide()
            });
            window.setTimeout(function () {
                page.render(output);
                phantom.exit();
            }, 2000);
        }, 2000);
    }
});
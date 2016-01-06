"use strict";
var page = require('webpage').create(),
    system = require('system');

function extractDomain(url) {
    var domain;
    if (url.indexOf("://") > -1) {
        domain = url.split('/')[2];
    }
    else {
        domain = url.split('/')[0];
    }
    domain = domain.split(':')[0];
    return domain;
}

var address = system.args[1];
var output = system.args[2];
var sessionId = system.args[3];
var domain = extractDomain(address);

phantom.addCookie({
    'name': 'JSESSIONID',
    'value': sessionId,
    'domain': domain
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
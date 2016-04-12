"use strict";
var page = require('webpage').create(),
    system = require('system'),
    common = require('./pdf-common');

page.paperSize = {
    format: 'A4',
    orientation: 'portrait',
    footer: {
        height: "1cm",
        contents: phantom.callback(function(pageNum, numPages) {
            return "<span style='float:right; font-size: 10pt'>" + pageNum + " / " + numPages + "</span>";
        })
    }
};

var address = system.args[1];
var output = system.args[2];
var sessionId = system.args[3];

common.addCookie(address, sessionId);
common.monitorResponses(page, address, onLoaded);

function onLoaded() {
    console.log("message loaded, prepare to render");
    window.setTimeout(function () {
        page.evaluate(function () {
            $('body').css('background', '#FFFFFF');
            $('.btn-download').hide();
            $('.mmia-form').css('zoom', 0.50);
            $('.mmia-form').css('width', 994);
            $('.mmia-form td').css('line-height', 1.20);
            $('.patient-section').css('margin-left', '11%');
            $('.content.rnr-content').css('-webkit-box-shadow', '0 0 0px rgba(255, 255, 255, 0)');
            $('.content.rnr-content').css('-moz-box-shadow', '0 0 0px rgba(255, 255, 255, 0)');
            $('.content.rnr-content').css('box-shadow', '0 0 0px rgba(255, 255, 255, 0)');
        });
        page.render(output);
        phantom.exit();
    }, 200);
}
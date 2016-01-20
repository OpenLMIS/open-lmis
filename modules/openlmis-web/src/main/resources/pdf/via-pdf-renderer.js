"use strict";
var page = require('webpage').create(),
    system = require('system'),
    common = require('./pdf-common');

var address = system.args[1];
var output = system.args[2];
var sessionId = system.args[3];

page.viewportSize = {width: 1680, height: 1250};

common.addCookie(address, sessionId);
common.monitorResponses(page, address, onLoaded);

function onLoaded() {
    console.log("message loaded, prepare to render");
    window.setTimeout(function () {
        adjustPageForRender();
        page.render(output);
        phantom.exit();
    }, 200);
}

function adjustPageForRender() {
    page.evaluate(function () {

        function capturePages() {
            var viaPages = [];

            function saveCurrentPage() {
                $(".rnr-content").css('page-break-after', 'always');
                viaPages.push($(".rnr-content").clone());
            }

            saveCurrentPage();

            var nextPageLink = $('#nextPageLink');
            while (nextPageLink.parent().attr('class') != 'disabled') {
                nextPageLink.click();
                saveCurrentPage();
            }
            return viaPages;
        }

        function combineMultiplePages(pages) {
            var body = $(document.body);
            body.empty();
            pages.forEach(function (viaPage) {
                body.append(viaPage);
            });
            $('.btn-download').hide();
            $('.pagination').hide();
            $('.page-num-pdf').show();
        }

        var pages = capturePages();
        combineMultiplePages(pages);
    });
}

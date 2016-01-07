"use strict";
var page = require('webpage').create(),
    system = require('system');

var address = system.args[1];
var output = system.args[2];
var sessionId = system.args[3];
var domain = extractDomain(address);

phantom.addCookie({
    'name': 'JSESSIONID',
    'value': sessionId,
    'domain': domain
});

page.onResourceReceived = function (response) {
    if (response.stage === 'end') {
        console.log(response.url + "   " + response.stage);
        if (hasSwitchedToPtLanguage(response)) {
            renderPDF();
        }
        if (isXhrFinished(response)) {
            tryToSwitchToPtLanguage();
        }
    }
};

page.onConsoleMessage = function (msg, lineNum, sourceId) {
    console.log('CONSOLE: ' + msg + ' (from line #' + lineNum + ' in "' + sourceId + '")');
};

page.open(address, function (status) {
    console.log(status);
    if (status !== 'success') {
        console.log('Unable to load the address!');
        phantom.exit(1);
    }
});

function responseUrlContains(response, str) {
    return response.url.indexOf(str) > -1;
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

        function combineMutiplePages(pages) {
            var body = $(document.body);
            body.empty();
            pages.forEach(function (viaPage) {
                body.append(viaPage);
            });
            $('.text-align-right').hide();
        }

        var pages = capturePages();
        combineMutiplePages(pages);
    });
}

function renderPDF() {
    console.log("message loaded, prepare to render");

    window.setTimeout(function () {
        adjustPageForRender();
        page.render(output);
        phantom.exit();
    }, 200);
}

function extractDomain(url) {
    var domain;
    if (url.indexOf("://") > -1) {
        domain = url.split('/')[2];
    } else {
        domain = url.split('/')[0];
    }
    domain = domain.split(':')[0];
    return domain;
}

function tryToSwitchToPtLanguage() {
    window.setTimeout(function () {
        page.evaluate(function () {
            console.log("trying to switch language to pt");
            var ptButton = $("#locale_pt");
            if ($(ptButton[0]).attr("id") !== undefined) {
                ptButton.click();
                console.log("found pt button, clicked");
            } else {
                console.log("can not find pt button");
            }
        });
    }, 100);
}

function hasSwitchedToPtLanguage(response) {
    //this request is the pt language json, we can only render pdf after it's loaded
    return responseUrlContains(response, 'messages.json');
}

function isXhrFinished(response) {
    //the following 3 requests are critical for the page to render, we need to wait
    //for them to finish loading then we can click pt button
    return responseUrlContains(response, 'reports.html') ||
        responseUrlContains(response, 'skipped.json') ||
        responseUrlContains(response, 'locales.json');
}

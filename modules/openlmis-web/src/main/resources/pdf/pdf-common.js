"use strict";

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

function responseUrlContains(response, str) {
    return response.url.indexOf(str) > -1;
}

function tryToSwitchToPtLanguage(page) {
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
            $(".toggleFullScreen").hide()
        });
    }, 100);
}

function monitorResponses(page, address, onLoaded) {
    page.onConsoleMessage = function (msg, lineNum, sourceId) {
        console.log('CONSOLE: ' + msg + ' (from line #' + lineNum + ' in "' + sourceId + '")');
    };

    page.onResourceReceived = function (response) {
        if (response.stage === 'end') {
            console.log(response.url + "   " + response.stage);
            if (hasSwitchedToPtLanguage(response)) {
                onLoaded();
            }
            if (isXhrFinished(response)) {
                tryToSwitchToPtLanguage(page);
            }
        }
    };

    page.open(address, function (status) {
        console.log(status);
        if (status !== 'success') {
            console.log('Unable to load the address!');
            phantom.exit(1);
        }
    });
}

function addCookie(url, sessionId) {
    var domain = extractDomain(url);

    phantom.addCookie({
        'name': 'JSESSIONID',
        'value': sessionId,
        'domain': domain
    });
}

exports.monitorResponses = monitorResponses;
exports.addCookie = addCookie;
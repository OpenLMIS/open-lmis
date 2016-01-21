"use strict";
var finishedRequestUrls = [];
var triedSwitch = false;

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

function isLanguageJsonLoaded() {
    //this request is the pt language json, we can only render pdf after it's loaded
    return lastResponseUrlContains('messages.json');
}

function isXhrFinished() {
    //the following 3 requests are critical for the page to render, we need to wait
    //for them to finish loading then we can click pt button
    return responseUrlContains('reports.html') &&
        responseUrlContains('skipped.json') &&
        responseUrlContains('locales.json');
}

function responseUrlContains(str) {
    return finishedRequestUrls.some(function (elem) {
        return elem.indexOf(str) > -1;
    });
}

function lastResponseUrlContains(str) {
    return finishedRequestUrls[finishedRequestUrls.length - 1].indexOf(str) > -1;
}

function tryToSwitchToPtLanguage(page) {
    window.setTimeout(function () {
        page.evaluate(function () {
            console.log("trying to switch language to pt");
            var ptButton = $("#locale_pt");
            if ($(ptButton[0]).attr("id") !== undefined) {
                ptButton.click();
                $(".toggleFullScreen").hide();
                console.log("found pt button, clicked");
            } else {
                console.log("can not find pt button");
            }
        });
        triedSwitch = true;
    }, 100);
}

function monitorResponses(page, address, onLoaded) {
    page.onConsoleMessage = function (msg, lineNum, sourceId) {
        console.log('CONSOLE: ' + msg + ' (from line #' + lineNum + ' in "' + sourceId + '")');
    };

    page.onResourceReceived = function (response) {
        if (response.stage === 'end') {
            console.log(response.url + "   " + response.stage);
            finishedRequestUrls.push(response.url);

            if (!triedSwitch && isXhrFinished()) {
                tryToSwitchToPtLanguage(page);
            }

            if (triedSwitch && isLanguageJsonLoaded()) {
                onLoaded();
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
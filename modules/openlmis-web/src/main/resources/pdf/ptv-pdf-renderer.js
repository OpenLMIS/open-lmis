"use strict";
var page = require('webpage').create(),
  system = require('system'),
  common = require('./pdf-common');

page.paperSize = {
  format: 'A3',
  orientation: 'portrait',
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
      $('.content.rnr-content').css('-webkit-box-shadow', '0 0 0px rgba(255, 255, 255, 0)');
      $('.content.rnr-content').css('-moz-box-shadow', '0 0 0px rgba(255, 255, 255, 0)');
      $('.content.rnr-content').css('box-shadow', '0 0 0px rgba(255, 255, 255, 0)');

      $('.al-form').css('width', 994);
      $('.al-form').css('zoom', 0.50);
      $('.al-form td').css('line-height', 1.0);

    });
    page.render(output);
    phantom.exit();
  }, 200);
}
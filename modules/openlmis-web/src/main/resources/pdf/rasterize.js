"use strict";
var page = require('webpage').create(),
  system = require('system');

var address = system.args[1];
var output = system.args[2];

phantom.addCookie({
  'name': 'JSESSIONID',
  'value': '8auczhujfm3cn06dhsp05elp',
  'domain': 'localhost'
});

page.open(address, function(status) {
  
  if (status !== 'success') {
    console.log('Unable to load the address!');
    phantom.exit(1);
  } else {
    // page.viewportSize = {
    //   width: 1200,
    //   height: 800
    // };
    page.evaluate(function() {
      $(".toggleFullScreen").hide()
    });
    window.setTimeout(function() {
      page.render(output);
      phantom.exit();
    }, 200);
  }
});
//cookie, address, pdf file path, ubuntu run

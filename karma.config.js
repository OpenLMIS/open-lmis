// Karma configuration
// Generated on Thu Nov 28 2013 09:59:55 GMT+0530 (IST)

module.exports = function(config) {
  config.set({

    // base path, that will be used to resolve files and exclude
    basePath: '',


    // frameworks to use
    frameworks: ['jasmine'],


    // list of files / patterns to load in the browser
    files: [
      'modules/openlmis-web/src/main/webapp/public/lib/jquery/jquery-1.8.2.min.js',
      'modules/openlmis-web/src/main/webapp/public/lib/angular/angular.min.js',
      'modules/openlmis-web/src/main/webapp/public/lib/angular/angular-cookies.min.js',
      'modules/openlmis-web/src/main/webapp/public/lib/angular-ui/bootstrap/ui-bootstrap-0.1.0.min.js',
      'modules/openlmis-web/src/main/webapp/public/lib/angular-ui/angular-ui.js',
      'modules/openlmis-web/src/test/javascript/specs/**/*.js'
    ],


    // list of files to exclude
    exclude: [
      
    ],


    // test results reporter to use
    // possible values: 'dots', 'progress', 'junit', 'growl', 'coverage'
    reporters: ['progress'],


    // web server port
    port: 9876,


    // enable / disable colors in the output (reporters and logs)
    colors: true,


    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_INFO,


    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,


    // Start these browsers, currently available:
    // - Chrome
    // - ChromeCanary
    // - Firefox
    // - Opera (has to be installed with `npm install karma-opera-launcher`)
    // - Safari (only Mac; has to be installed with `npm install karma-safari-launcher`)
    // - PhantomJS
    // - IE (only Windows; has to be installed with `npm install karma-ie-launcher`)
    browsers: ['PhantomJS'],


    // If browser does not capture in given timeout [ms], kill it
    captureTimeout: 60000,


    // Continuous Integration mode
    // if true, it capture browsers, run tests and exit
    singleRun: false
  });
};

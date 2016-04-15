// Karma configuration
// Generated on Thu Nov 28 2013 09:59:55 GMT+0530 (IST)

module.exports = function (config) {
  config.set({

    // base path, that will be used to resolve files and exclude
    basePath: 'src/main/webapp/public',

    // frameworks to use
    frameworks: ['jasmine'],

    // list of files / patterns to load in the browser
    files: [
      //Library files in order
      'lib/jquery/jquery-2.0.0.min.js',
      'lib/jquery/jquery-ui-1.9.2.custom.min.js',
      'lib/angular/angular.min.js',
      'lib/angular/angular-route.min.js',
      'lib/angular/angular-resource.min.js',
      'lib/angular/angular-cookies.min.js',
      'lib/angular-ui/ng-grid-1.6.3.js',
      'lib/angular-ui/bootstrap/ui-bootstrap-0.1.0.min.js',
      'lib/angular-ui/angular-ui.js',
      'lib/indexed-db-angular-service/js/services/indexed-db.js',
      'lib/localstorage/localStorage.js',
      'lib/underscore/underscore-min.js',
      'lib/base2.js',
      'lib/select2/select2.min.js',
      'lib/ui-calendar/calendar.js',
      'lib/easypiechart/angular.easypiechart.min.js',
      'lib/angular-ui/ui-jqplot/ui-jqplot-chart.js',
      'lib/angular-ui/ng-table/ng-table.js',

      //Mocking library
      '../../../test/javascript/lib/angular-mocks.js',

      //Source files
      'js/shared/*.js',
      'js/shared/services/services.js',
      'js/shared/**/*.js',

      'js/*/controller/*.js',
      'js/*/module/*.js',

      'js/**/*.js',

      //Spec files
      '../../../test/javascript/specs/**/*.js'
    ],

    // list of files to exclude
    exclude: [],

    plugins: [
      'karma-jasmine',
      'karma-coverage',
      'karma-firefox-launcher'
    ],

    // test results reporter to use
    // possible values: 'dots', 'progress', 'junit', 'growl', 'coverage'
    reporters: ['progress', 'coverage'],

    coverageReporter: {
      type: 'html',
      dir: '../../../test/coverage/'
    },

    preprocessors: {
      'js/**/*.js': ['coverage']
    },

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
    browsers: ['Firefox'],

    // If browser does not capture in given timeout [ms], kill it
    captureTimeout: 30000,

    // Continuous Integration mode
    // if true, it capture browsers, run tests and exit
    singleRun: true
  });
};

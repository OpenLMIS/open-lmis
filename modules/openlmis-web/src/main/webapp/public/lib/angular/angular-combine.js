/*! angular-combine - v0.1.1 - 2013-12-11 */
"use strict";

angular.module("angularCombine", []), angular.module("angularCombine").provider("angularCombineConfig", function() {
  var config = [];
  this.addConf = function(regexp, combinedUrl) {
    console.log("Add conf to angularCombine", regexp, combinedUrl), config.push({
      regexp: regexp,
      combinedUrl: combinedUrl
    });
  }, this.$get = function() {
    return config;
  };
}), angular.module("angularCombine").config([ "$provide", function($provide) {
  $provide.decorator("$templateCache", [ "$delegate", "$http", "$injector", function($delegate, $http, $injector) {
    var idx, conf, origGetMethod = $delegate.get, angularCombineConfig = $injector.get("angularCombineConfig"), loadCombinedTemplates = function(combinedUrl) {
      var combinedTplPromise;
      return function(url) {
        return combinedTplPromise || (console.log("fetching all templates combined into ", combinedUrl),
            combinedTplPromise = $http.get(combinedUrl).then(function(response) {
              return $injector.get("$compile")(response.data), response;
            })), combinedTplPromise.then(function(response) {
          return {
            status: response.status,
            data: origGetMethod(url)
          };
        });
      };
    };
    for (idx in angularCombineConfig) conf = angularCombineConfig[idx], conf.load = loadCombinedTemplates(conf.combinedUrl);
    return $delegate.get = function(url) {
      for (idx in angularCombineConfig) if (conf = angularCombineConfig[idx], conf.regexp.test(url)) return conf.load(url);
      return origGetMethod(url);
    }, $delegate;
  } ]);
} ]);
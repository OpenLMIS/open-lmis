/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

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
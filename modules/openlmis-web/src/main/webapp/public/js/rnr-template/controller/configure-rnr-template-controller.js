/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function ConfigureRnRTemplateController($scope, programs, $location) {

  $scope.programs = programs;

  $scope.configure = function (id) {
    $location.path('/create-rnr-template/' + id);
  };
}

ConfigureRnRTemplateController.resolve = {
  programs:function ($q, PullPrograms, $location, $route, $timeout) {
    var deferred = $q.defer();

    $timeout(function () {
      PullPrograms.get({}, function (data) { //success
        deferred.resolve(data.programs);
      }, function () {
        location.path('/select-program');
      });
    }, 100);

    return deferred.promise;
  }
};




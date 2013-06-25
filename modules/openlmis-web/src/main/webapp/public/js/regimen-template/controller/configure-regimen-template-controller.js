/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function ConfigureRegimenTemplateController($scope, programs, $location) {
  $scope.programs = programs;

  $scope.configure = function(programId) {
    $location.path('/create-regimen-template/' + programId);
  };
}

ConfigureRegimenTemplateController.resolve = {
  programs:function ($q, Programs, $location, $route, $timeout) {
    var deferred = $q.defer();

    $timeout(function () {
      Programs.get({}, function (data) { //success
        deferred.resolve(data.programs);
      }, function () {
        location.path('/select-program');
      });
    }, 100);

    return deferred.promise;
  }
};

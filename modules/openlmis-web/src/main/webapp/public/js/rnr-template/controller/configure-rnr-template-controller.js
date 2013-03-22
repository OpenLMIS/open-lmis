/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function ConfigureRnRTemplateController($scope, programs, $location) {

  $scope.programs = programs;

  $scope.createRnrTemplate = function () {
    if ($scope.$parent.program != undefined) {
      $scope.error = "";
      $location.path('/create-rnr-template/' + $scope.$parent.program.id);
    } else {
      $scope.error = "Please select a program";
    }
  };
};

ConfigureRnRTemplateController.resolve = {
  programs:function ($q, ActivePrograms, $location, $route, $timeout) {
    var deferred = $q.defer();

    $timeout(function () {
      ActivePrograms.get({}, function (data) { //success
        deferred.resolve(data.programList);
      }, function () {
        location.path('/select-program');
      });
    }, 100);

    return deferred.promise;
  }
};




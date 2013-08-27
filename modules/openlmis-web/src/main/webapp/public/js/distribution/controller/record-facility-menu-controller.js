/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function RecordFacilityMenuController($scope, $location, $routeParams, distributionService) {

  $scope.$on('distributionReceived', function () {
    $scope.distribution = distributionService.distribution;
  });

  if ($scope.distribution == undefined) {
    $scope.distribution = distributionService.distribution;
  }

  $scope.changeRoute = function (routeName) {
    $location.path($location.$$path.replace(getURLName(), routeName));
  }

  $scope.getRefrigeratorStatus = function () {
    if (!isUndefined($scope.distribution)) {
      var readingList = $scope.distribution.facilityDistributionData[$routeParams.facility].refrigeratorReadings;
      if (_.findWhere(readingList, {status: undefined})) {
        return 'is-incomplete';
      }
      if (_.findWhere(readingList, {status: 'is-incomplete'})) {
        return 'is-incomplete';
      }
      if (_.findWhere(readingList, {status: 'is-empty'})) {
        if (_.findWhere(readingList, {status: 'is-complete'})) {
          return 'is-incomplete';
        }
        return 'is-empty';
      }
      return 'is-complete';
    }
  };

  $scope.getEPIInventoryStatus = function () {
    return 'is-empty';
  };

  $scope.getEPIUseStatus = function () {
    return 'is-empty';
  };

  function getURLName() {
    var urlParts = $location.$$path.split('/');
    return urlParts[urlParts.length - 1];
  }

  $scope.isSelected = function (route) {
    var routeName = getURLName();
    if (routeName == route) {
      return 'selected';
    }
  }

};
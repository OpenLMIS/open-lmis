/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function RecordFacilityMenuController($scope, $location, $routeParams, distributionService) {

  $scope.distributionData = distributionService.distribution.facilityDistributionData[$routeParams.facility];

  $scope.changeRoute = function (routeName) {
    $location.path($location.$$path.replace(getURLName(), routeName));
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

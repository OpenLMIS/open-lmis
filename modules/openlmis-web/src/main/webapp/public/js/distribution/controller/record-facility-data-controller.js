/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function RecordFacilityDataController($scope, facilities, $location, $routeParams) {

  $scope.facilities = facilities;

  $scope.format = function (facility) {
    if (facility.id) {
      return "<div class='is-empty'>" +
        "<span class='status-icon'></span>" + facility.text +
        "</div>";
    } else {
      return facility.text;
    }
  }

  $scope.chooseFacility = function () {
    $location.path('record-facility-data/' + $routeParams.distribution + '/' + $scope.facilitySelected.id + '/refrigerator-data');
  }

}


RecordFacilityDataController.resolve = {

  facilities: function ($q, $timeout, IndexedDB, $route) {
    var waitOn = $q.defer();
    var distributionId = $route.current.params.distribution;

    IndexedDB.get('distributionReferenceData', utils.parseIntWithBaseTen(distributionId), function (event) {
      waitOn.resolve(event.target.result.facilities);
    }, {});

    return waitOn.promise;
  }
};





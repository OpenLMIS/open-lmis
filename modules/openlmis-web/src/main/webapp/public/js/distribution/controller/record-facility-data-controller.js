/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function RecordFacilityDataController($scope, $location, $routeParams, IndexedDB) {
  $scope.label = $routeParams.facility ? 'label.change.facility' : "label.select.facility";

  IndexedDB.get('distributions', utils.parseIntWithBaseTen($routeParams.distribution), function (e) {
    $scope.distribution = e.target.result;
  }, {});

  IndexedDB.get('distributionReferenceData', utils.parseIntWithBaseTen($routeParams.distribution), function (event) {
    $scope.facilities = event.target.result.facilities;
    $scope.facilitySelected = _.findWhere($scope.facilities, {id: utils.parseIntWithBaseTen($routeParams.facility)});
  }, {});

  $scope.format = function (facility) {
    if (facility.id) {
      return "<div class='is-empty'>" +
          "<span class='status-icon'></span>" + facility.text +
          "</div>";
    } else {
      return facility.text;
    }
  };

  $scope.chooseFacility = function () {
    if (!$scope.facilitySelected || !$scope.facilitySelected.id) {
      $location.path('record-facility-data/' + $routeParams.distribution);
      return;
    }
    $location.path('record-facility-data/' + $routeParams.distribution + '/' + $scope.facilitySelected.id + '/refrigerator-data');
  }

}
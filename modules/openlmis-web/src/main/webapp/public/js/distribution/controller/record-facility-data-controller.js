/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function RecordFacilityDataController($scope, $location, $routeParams, IndexedDB, distributionService) {
  $scope.label = $routeParams.facility ? 'label.change.facility' : "label.select.facility";

  $scope.distribution = distributionService.distribution;

  IndexedDB.get('distributionReferenceData', utils.parseIntWithBaseTen($routeParams.distribution), function (event) {
    var facilities = event.target.result.facilities;
    $scope.zoneFacilityMap = _.groupBy(facilities, function (facility) {
      return facility.geographicZone.code;
    });
    $scope.geographicZones = _.uniq(_.pluck(facilities, 'geographicZone'), function (zone) {
      return zone.code;
    });
    $scope.facilitySelected = _.findWhere(facilities, {id: utils.parseIntWithBaseTen($routeParams.facility)});
  }, {});

  $scope.format = function (dropDownObj) {
    if (dropDownObj.element[0].value) {
      var facilityId = utils.parseIntWithBaseTen(dropDownObj.element[0].value);
      return "<div class='" + $scope.distribution.facilityDistributionData[facilityId].computeStatus() + "'>" +
        "<span class='status-icon'></span>" + dropDownObj.text +
        "</div>";
    } else {
      return dropDownObj.text;
    }
  };

  $scope.chooseFacility = function () {
    if ($routeParams.facility != $scope.facilitySelected.id)
      $location.path('record-facility-data/' + $routeParams.distribution + '/' + $scope.facilitySelected.id + '/refrigerator-data');
  }

}

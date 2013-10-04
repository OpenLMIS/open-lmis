/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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
        "<span id="+ facilityId +" class='status-icon'></span>" + dropDownObj.text +
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

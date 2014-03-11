/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function RefrigeratorController($scope, $dialog, IndexedDB, $routeParams, distributionService) {
  $scope.distribution = distributionService.distribution;
  $scope.selectedFacilityId = $routeParams.facility;

  $scope.edit = {};

  $scope.setEdit = function (serialNum, index) {
    angular.forEach($scope.edit, function (value, key) {
      $scope.edit[key] = false;
    });
    $scope.edit[serialNum] = true;
    if (!isUndefined(index)) {
      var refrigeratorEditButton = angular.element('#editReading' + index).offset().top;
      angular.element('body,html').animate({scrollTop: refrigeratorEditButton + 'px'}, 'slow', function () {
        $('input[name^="temperature"]:visible').focus();
      });
    }
  };

  $scope.showRefrigeratorModal = function () {
    $scope.addRefrigeratorModal = true;
    $scope.newRefrigeratorReading = null;
    $scope.isDuplicateSerialNumber = false;
  };

  $scope.addRefrigeratorToStore = function () {
    var exists = _.find($scope.distribution.facilityDistributions[$scope.selectedFacilityId].refrigerators.readings,
      function (reading) {
        return reading.refrigerator.serialNumber.toLowerCase() === $scope.newRefrigeratorReading.refrigerator.serialNumber.toLowerCase();
      });
    if (exists) {
      $scope.isDuplicateSerialNumber = true;
      return;
    }
    $scope.distribution.facilityDistributions[$scope.selectedFacilityId].refrigerators.addReading(angular.copy($scope.newRefrigeratorReading));

    IndexedDB.put('distributions', $scope.distribution);

    $scope.addRefrigeratorModal = $scope.isDuplicateSerialNumber = undefined;
  };

  $scope.isFormDisabled = function () {
    return ($scope.distribution.facilityDistributions[$scope.selectedFacilityId].status === DistributionStatus.SYNCED) ||
        ($scope.distribution.facilityDistributions[$scope.selectedFacilityId].facilityVisit.visited === false);
  };

  $scope.showDeleteRefrigeratorConfirmationModel = function (serialNumberToDelete) {
    var dialogOpts = {
      id: "deleteRefrigeratorInfo",
      header: 'delete.refrigerator.readings.header',
      body: 'delete.refrigerator.readings.confirm'
    };

    var callback = function (serialNumberToDelete) {
      return function (result) {
        if (!result) return;
        $scope.distribution.facilityDistributions[$scope.selectedFacilityId].refrigerators.readings =
          _.reject($scope.distribution.facilityDistributions[$scope.selectedFacilityId].refrigerators.readings,
            function (refrigeratorReading) {
              return serialNumberToDelete == refrigeratorReading.refrigerator.serialNumber;
            });
        IndexedDB.put('distributions', $scope.distribution);
      };
    };

    OpenLmisDialog.newDialog(dialogOpts, callback(serialNumberToDelete), $dialog);
  };

  $scope.showProblemsDiv = function (idSent) {
    setTimeout(function () {
      $('body,html').animate({
        scrollTop: utils.parseIntWithBaseTen($('#' + idSent).offset().top) + 'px'
      }, 'fast');
    }, 0);
  };
}

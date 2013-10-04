/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function RefrigeratorController($scope, $dialog, messageService, IndexedDB, $routeParams, distributionService) {

  $scope.distribution = distributionService.distribution;
  $scope.selectedFacilityId = $routeParams.facility;

  $scope.edit = {};

  $scope.setEdit = function (serialNum, index) {
    angular.forEach($scope.edit, function (value, key) {
      $scope.edit[key] = false;
    });
    $scope.edit[serialNum] = true;
    if (!isUndefined(index)) {
      var refrigeratorEditButton = angular.element('#editReading'+index).offset().top;
      angular.element('body,html').animate({scrollTop : refrigeratorEditButton+'px'},'slow',function(){
        $('input[name^="temperature"]:visible').focus();
      });
    }
  };

  $scope.showRefrigeratorModal = function () {
    $scope.addRefrigeratorModal = true;
    $scope.newRefrigerator = null;
  };

  $scope.addRefrigeratorToStore = function () {
    var exists = _.find($scope.distribution.facilityDistributionData[$scope.selectedFacilityId].refrigerators.refrigeratorReadings,
      function (reading) {
        return reading.refrigerator.serialNumber.toLowerCase() === $scope.newRefrigerator.serialNumber.toLowerCase();
      });
    if (exists) {
      $scope.isDuplicateSerialNumber = true;
      return;
    }
    $scope.distribution.facilityDistributionData[$scope.selectedFacilityId].refrigerators.
      addRefrigerator({'refrigerator': angular.copy($scope.newRefrigerator), status: "is-empty"});

    IndexedDB.put('distributions', $scope.distribution);

    $scope.addRefrigeratorModal = $scope.isDuplicateSerialNumber = undefined;
  };

  $scope.showDeleteRefrigeratorConfirmationModel = function (serialNumberToDelete) {
    var dialogOpts = {
      id: "deleteRefrigeratorInfo",
      header: messageService.get('delete.refrigerator.readings.header'),
      body: messageService.get('delete.refrigerator.readings.confirm')
    };

    var callback = function (serialNumberToDelete) {
      return function (result) {
        if (!result) return;
        $scope.distribution.facilityDistributionData[$scope.selectedFacilityId].refrigerators.refrigeratorReadings =
          _.reject($scope.distribution.facilityDistributionData[$scope.selectedFacilityId].refrigerators.refrigeratorReadings,
            function (refrigeratorReading) {
              return serialNumberToDelete == refrigeratorReading.refrigerator.serialNumber;
            });
        IndexedDB.put('distributions', $scope.distribution);
      };
    };

    OpenLmisDialog.newDialog(dialogOpts, callback(serialNumberToDelete), $dialog, messageService);
  };


}

function showProblemDivAnimation(idSent){
  $('body,html').animate({
     scrollTop : parseInt($('#'+idSent).offset().top)+'px'
  },'fast');
}
function showProblemsDiv(idSent){
  setTimeout(showProblemDivAnimation(idSent),100)
}




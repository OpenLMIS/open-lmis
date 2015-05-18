/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function EquipmentInventoryController($scope, UserFacilityList, EquipmentInventories, ManageEquipmentInventoryProgramList, ManageEquipmentInventoryFacilityProgramList, EquipmentTypesByProgram, EquipmentOperationalStatus, $routeParams, messageService, UpdateEquipmentInventoryStatus, $timeout) {

  $scope.loadPrograms = function (initialLoad) {
    // Get home facility for user
    UserFacilityList.get({}, function (data) {
      $scope.myFacility = data.facilityList[0];
      if ($scope.myFacility) {
        // Home facility found, show home facility
        $scope.facilityDisplayName = $scope.myFacility.code + ' - ' + $scope.myFacility.name;

        // Home facility found and my facility type selected, get home facility programs
        if ($scope.selectedType === "0") {
          ManageEquipmentInventoryFacilityProgramList.get({facilityId: $scope.myFacility.id}, function (data) {
            $scope.programs = data.programs;
            if (initialLoad) {
              $scope.selectedProgram = _.where($scope.programs, {id: parseInt($routeParams.program,10)})[0];
              $scope.loadEquipmentTypes(initialLoad);
            }
          }, {});
        }
      } else {
        // Home facility not found, show none assigned message
        $scope.facilityDisplayName = messageService.get("label.none.assigned");
      }
    }, {});

    // Supervised facility type selected, get supervised facility programs
    if ($scope.selectedType === "1") {
      ManageEquipmentInventoryProgramList.get({}, function (data) {
        $scope.programs = data.programs;
        if (initialLoad) {
          $scope.selectedProgram = _.where($scope.programs, {id: parseInt($routeParams.program, 10)})[0];
          $scope.loadEquipmentTypes(initialLoad);
        }
      }, {});
    }
  };

  $scope.loadEquipmentTypes = function (initialLoad) {
    EquipmentTypesByProgram.get({programId: $scope.selectedProgram.id}, function (data) {
      $scope.equipmentTypes = data.equipment_types;
      if (initialLoad) {
        $scope.selectedEquipmentType = _.where($scope.equipmentTypes, {id: parseInt($routeParams.equipmentType,10)})[0];
        $scope.loadInventory();
      }
    }, {});
  };

  $scope.loadInventory = function () {
    if ($scope.selectedProgram && $scope.selectedEquipmentType) {
      EquipmentInventories.get({
        typeId: $scope.selectedType,
        programId: $scope.selectedProgram.id,
        equipmentTypeId: $scope.selectedEquipmentType.id
      }, function (data) {
        $scope.inventory = data.inventory;
        $scope.groups = _.groupBy($scope.inventory, function (item) {
          return item.facility.geographicZone.parent.name;
        });
        for (var prop in $scope.groups) {
          $scope.groups[prop] = _.groupBy($scope.groups[prop], getGeographicZone);
        }
      });
    }
  };

  $scope.changeFacilityType = function () {
    $scope.programs = undefined;
    $scope.selectedProgram = undefined;
    $scope.equipmentTypes = undefined;
    $scope.selectedEquipmentType = undefined;

    $scope.loadPrograms();
  };

  $scope.changeProgram = function () {
    $scope.equipmentTypes = undefined;
    $scope.selectedEquipmentType = undefined;

    $scope.loadEquipmentTypes();
  };

  function getGeographicZone(item) {
    return item.facility.geographicZone.name;
  }

  $scope.selectedType = $routeParams.from || "0";
  $scope.loadPrograms(true);

  EquipmentOperationalStatus.get(function(data){
    $scope.operationalStatusList = data.status;
  });

  $scope.updateStatus = function (item) {
    if (item.prevStatusId && item.prevStatusId !== item.operationalStatusId) {
      UpdateEquipmentInventoryStatus.save({}, item, function (data) {
        // Success
        item.showSuccess = true;
        $timeout(function () {
          item.showSuccess = false;
        }, 3000);
      });
    }
    item.prevStatusId = item.operationalStatusId;
  };

  $scope.getAge = function (yearOfInstallation) {
    return (new Date().getFullYear()) - yearOfInstallation;
  };

}
/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

function CreateEquipmentInventoryController($scope, $location, $routeParams, EquipmentInventory, Donors, EquipmentsByType, SaveEquipmentInventory, UserFacilityList, EquipmentOperationalStatus, messageService, EquipmentType, EquipmentInventoryFacilities, EquipmentEnergyTypes) {

  $scope.$parent.message = $scope.$parent.error = '';

  $scope.max_year = new Date().getFullYear();
  $scope.submitted = false;
  $scope.showError = false;

  $scope.from = $routeParams.from;
  $scope.manufacturers = [];
  $scope.models = [];
  $scope.selected = {};

  EquipmentsByType.get({equipmentTypeId: $routeParams.equipmentType}, function (data) {
    $scope.equipments = data.equipments;
    $scope.manufacturers = _.uniq(_.pluck($scope.equipments, 'manufacturer'));
  });

  EquipmentType.get({id: $routeParams.equipmentType}, function (data) {
    $scope.equipmentType = data.equipment_type;
    if (!$scope.inventory) {
      $scope.inventory = {};
    }
    if (!$scope.inventory.equipment) {
      $scope.inventory.equipment = {};
    }
    $scope.inventory.equipment.equipmentType = $scope.equipmentType;
    $scope.inventory.equipment.equipmentTypeId = $scope.equipmentType.id;
  }, {});

  if ($routeParams.id === undefined) {
    $scope.screenType = 'create';
    if (!$scope.inventory) {
      $scope.inventory = {};
    }
    if (!$scope.inventory.equipment) {
      $scope.inventory.equipment = {};
    }
    $scope.inventory.programId = $routeParams.program;

    // set default of checkboxes so the submission does not become null and hence an error.
    $scope.inventory.replacementRecommended = false;
    $scope.inventory.isActive = true;
    // To match format UI expects, need to use ISO string and split out time from date
    var now = new Date();
    $scope.inventory.dateLastAssessedString = now.toISOString().split("T")[0];
    $scope.inventory.yearOfInstallation = now.getFullYear();

    if ($routeParams.from === "0") {
      // Create new inventory at my facility, show facility as readonly
      UserFacilityList.get({}, function(data){
        $scope.inventory.facility = data.facilityList[0];
        if ($scope.inventory.facility) {
          $scope.inventory.facilityId = data.facilityList[0].id;
          $scope.facilityDisplayName = $scope.inventory.facility.code + " - " + $scope.inventory.facility.name;
        }
      });
    } else {
      // Create new inventory at supervised facilities, facility drop down list
      EquipmentInventoryFacilities.get({programId: $routeParams.program}, function (data) {
        $scope.facilities = data.facilities;
      }, {});
    }
  } else {
    $scope.screenType = 'edit';

    EquipmentInventory.get({
      id: $routeParams.id
    }, function (data) {
      $scope.inventory = data.inventory;

      if ($routeParams.from === "0") {
        // Edit inventory at my facility, show facility as readonly
        // Facility is already set, so just set the display name
        $scope.facilityDisplayName = $scope.inventory.facility.code + " - " + $scope.inventory.facility.name;
      } else {
        // Edit inventory at supervised facilities, facility drop down list with default
        EquipmentInventoryFacilities.get({programId: $routeParams.program}, function (data) {
          $scope.facilities = data.facilities;
        }, {});
      }
    });
  }

  EquipmentOperationalStatus.get(function(data){
    $scope.labOperationalStatusList = _.where(data.status, {category: 'LAB'});
    $scope.cceOperationalStatusList = _.where(data.status, {category: 'CCE'});
    $scope.cceNotFunctionalStatusList = _.where(data.status, {category: 'CCE Not Functional'});
  });

  Donors.get(function(data){
    $scope.donors = data.donors;
  });

  EquipmentEnergyTypes.get(function (data) {
    $scope.energyTypes = data.energy_types;
  });

  $scope.updateModels = function () {
    $scope.models = _.pluck(_.where($scope.equipments, {manufacturer: $scope.selected.manufacturer}), 'model');

    // Also reset equipment fields
    $scope.selected.model = "";
    $scope.inventory.equipment = undefined;
    $scope.inventory.equipmentId = undefined;
  };

  $scope.updateEquipmentInfo = function () {
    if ($scope.selected.manufacturer && $scope.selected.model) {
      $scope.inventory.equipment = _.where($scope.equipments, {manufacturer: $scope.selected.manufacturer, model: $scope.selected.model})[0];
      $scope.inventory.equipmentId = $scope.inventory.equipment.id;
    }
  };

  $scope.checkForBadStatus = function () {
    var operationalStatus = _.where($scope.cceOperationalStatusList, {id: parseInt($scope.inventory.operationalStatusId, 10)})[0];
    $scope.badStatusSelected = operationalStatus.isBad;
  };

  $scope.saveInventory = function () {
    $scope.error = '';
    $scope.showError = true;
    if(!$scope.inventoryForm.$invalid ){
      // Need to set this for deserialization
      if ($scope.screenType === 'create') {
        if ($scope.equipmentType.coldChain === true) {
          $scope.inventory.equipment.equipmentTypeName = "coldChainEquipment";
        } else {
          $scope.inventory.equipment.equipmentTypeName = "equipment";
        }
      }

      if (!$scope.inventory.equipment.name) {
        $scope.inventory.equipment.name = $scope.inventory.equipment.manufacturer + " / " + $scope.inventory.equipment.model;
      }

      // When saving, need to make sure date fields are set from string date fields
      // Do this by parsing date string and add timezone offset seconds
      var now = new Date();
      if ($scope.inventory.dateDecommissionedString) {
        $scope.inventory.dateDecommissioned = Date.parse($scope.inventory.dateDecommissionedString) + (now.getTimezoneOffset()*60000);
      }

      if ($scope.inventory.dateLastAssessedString) {
        $scope.inventory.dateLastAssessed = Date.parse($scope.inventory.dateLastAssessedString) + (now.getTimezoneOffset()*60000);
      }

      SaveEquipmentInventory.save($scope.inventory, function (data) {
        $scope.$parent.message = messageService.get(data.success);
        $scope.$parent.selectedProgram = {id: $scope.inventory.programId};
        console.info($scope.$parent.selectedProgram);
        $location.path('/' + $routeParams.from + '/' + $scope.inventory.programId + '/' + $routeParams.equipmentType + '/' +
            $routeParams.page);
      }, function (data) {
        $scope.error = data.error;
      });
    }else{
      $scope.submitted = true;
      $scope.error = messageService.get('message.equipment.inventory.data.invalid');
    }
  };

  $scope.cancelCreateInventory = function () {
    $location.path('/' + $routeParams.from + '/' + $routeParams.program + '/' + $routeParams.equipmentType + '/' +
        $routeParams.page);
  };
}
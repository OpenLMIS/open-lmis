/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function FacilityController($scope, facilityReferenceData, $routeParams, facility, Facility, $location, FacilityProgramProducts, $q, $dialog, messageService) {
  $scope.$parent.facilityId = null;
  $scope.message = "";
  initialize();

  function initialize() {
    $scope.facilityTypes = facilityReferenceData.facilityTypes;
    $scope.geographicZones = facilityReferenceData.geographicZones;
    $scope.facilityOperators = facilityReferenceData.facilityOperators;
    $scope.programs = facilityReferenceData.programs;
    if ($routeParams.facilityId) {
      $scope.facility = getFacilityWithDateObjects(facility);
      $scope.originalFacilityCode = facility.code;
      $scope.originalFacilityName = facility.name;
      $scope.isEdit = true;
      updateProgramsToDisplay();
    } else {
      $scope.facility = {};
      updateProgramsToDisplay();
      $scope.facility.enabled = true;
    }
    $scope.facilityProgramProductsList = [];
  }

  function getFacilityWithDateObjects(facility) {
    angular.forEach(facility.supportedPrograms, function (supportedProgram) {
      if (supportedProgram.startDate) {
        supportedProgram.startDate = new Date(supportedProgram.startDate);
      }
    });

    facility.goLiveDate = new Date(facility.goLiveDate);
    if (facility.goDownDate) {
      facility.goDownDate = new Date(facility.goDownDate);
    }

    return facility;
  }

  $scope.showISAEditModal = function (supportedProgram) {
    $scope.currentProgram = supportedProgram.program;
    $scope.$broadcast('showISAEditModal');
  };

  $scope.cancelUserSave = function () {
    $location.path('#/search');
  };

  function saveAllocationProgramProducts() {
    var promises = [];

    var keys = _.keys($scope.facilityProgramProductsList);

    $(keys).each(function (index, key) {
      var deferred = $q.defer();
      promises.push(deferred.promise);

      var program = $scope.facilityProgramProductsList[key][0].program;

      FacilityProgramProducts.update({facilityId: $scope.facility.id, programId: program.id}, $scope.facilityProgramProductsList[key], function (data) {
        deferred.resolve();
      }, function () {
        deferred.reject({error: "error.facility.allocation.product.save", program: program.name});
      });
    });

    return promises;
  }

  $scope.saveFacility = function () {
    if ($scope.facilityForm.$error.pattern || $scope.facilityForm.$error.required) {
      $scope.showError = "true";
      $scope.error = messageService.get('form.error');
      $scope.message = "";
      return;
    }

    var facilitySaveCallback = function (data) {
      $scope.facility = data.facility;
      var promises = saveAllocationProgramProducts();

      $q.all(promises).then(function () {
        $scope.showError = "true";
        $scope.error = "";
        $scope.errorProgram = "";
        $scope.$parent.message = data.success;
        $scope.facility = getFacilityWithDateObjects(data.facility);
        $scope.$parent.facilityId = $scope.facility.id;
        $location.path('');
      }, function (error) {
        $scope.showError = "true";
        $scope.message = "";
        $scope.error = error.error;
        $scope.errorProgram = error.program;
      });
    };

    if (!$scope.isEdit) {
      Facility.save({}, $scope.facility, facilitySaveCallback, errorFunc);
    } else {
      Facility.update({id: $scope.facility.id}, $scope.facility, facilitySaveCallback, errorFunc);
    }
  };

  $scope.blurDateFields = function () {
    angular.element("input[ui-date]").blur();
  };

  $scope.addSupportedProgram = function (supportedProgram) {
    if (supportedProgram.active && !supportedProgram.editedStartDate) {
      $scope.showDateNotEnteredError = true;
      return;
    }
    supportedProgram.program = getProgramById(supportedProgram.program.id);
    $scope.facility.supportedPrograms.push(supportedProgram);
    $scope.showDateNotEnteredError = false;
    $scope.supportedProgram = undefined;
    updateProgramsToDisplay();
  };

  $scope.showConfirmDateChangeWindow = function (program) {
    window.program = program;
    if (getProgramById(program.program.id).push) {
      $scope.dateChangeCallback(true);
      return;
    }
    var dialogOpts = {
      id: "dateChangeConfirmModal",
      header: messageService.get('message.setProgramStartDate'),
      body: messageService.get('message.dateChangeConfirmMessage')
    };
    OpenLmisDialog.newDialog(dialogOpts, $scope.dateChangeCallback, $dialog, messageService);
  };

  $scope.dateChangeCallback = function (result) {
    if (result) {
      window.program.startDate = window.program.editedStartDate;
    } else {
      window.program.editedStartDate = window.program.startDate;
    }
  };

  $scope.showRemoveProgramConfirmDialog = function (supportedProgram) {
    $scope.selectedSupportedProgram = supportedProgram;
    var options = {
      id: "removeProgramConfirmDialog",
      header: messageService.get('delete.facility.program.header'),
      body: messageService.get('delete.facility.program.confirm', $scope.selectedSupportedProgram.program.name)
    };
    OpenLmisDialog.newDialog(options, $scope.removeSupportedProgramConfirm, $dialog, messageService);
  };

  $scope.removeSupportedProgramConfirm = function (result) {
    if (result) {
      $scope.removeSupportedProgram();
    }
    $scope.selectedSupportedProgram = undefined;
  };

  $scope.removeSupportedProgram = function () {
    $scope.facility.supportedPrograms = _.without($scope.facility.supportedPrograms, $scope.selectedSupportedProgram);
    updateProgramsToDisplay();
  };


  function getProgramById(id) {
    return (_.findWhere($scope.programs, {'id': id}));
  }

  var successFunc = function (data) {
    $scope.showError = "true";
    $scope.error = "";
    $scope.message = data.success;
    $scope.facility = getFacilityWithDateObjects(data.facility);
    $scope.originalFacilityCode = data.facility.code;
    $scope.originalFacilityName = data.facility.name;
  };

  var errorFunc = function (data) {
    $scope.showError = "true";
    $scope.message = "";
    $scope.error = data.data.error;
  };

  $scope.enableFacility = function (active) {
    $scope.activeConfirmModal = false;
    $scope.facility.active = active;
    Facility.restore({id: $scope.facility.id, active: active}, successFunc, errorFunc);
  };

  $scope.disableFacilityCallBack = function (result) {
    if (!result) return;
    Facility.remove({id: $scope.facility.id}, {}, successFunc, errorFunc);
  };

  $scope.showConfirmFacilityDisableWindow = function () {
    var dialogOpts = {
      id: "disableFacilityDialog",
      header: messageService.get('disable.facility.header'),
      body: messageService.get('disable.facility.confirm', $scope.originalFacilityName, $scope.originalFacilityCode)
    };
    OpenLmisDialog.newDialog(dialogOpts, $scope.disableFacilityCallBack, $dialog, messageService);
  };

  $scope.showConfirmFacilityEnable = function () {
    var dialogOpts = {
      id: "enableConfirmModal",
      header: messageService.get("create.facility.enableFacility"),
      body: "'{0}' / '{1}' will be enabled in the system.".format($scope.originalFacilityName, $scope.originalFacilityCode)
    };
    OpenLmisDialog.newDialog(dialogOpts, $scope.enableFacilityCallBack, $dialog, messageService);
  };

  $scope.enableFacilityCallBack = function (result) {
    if (!result) return;
    $scope.showConfirmFacilityActivate();
  };

  $scope.showConfirmFacilityActivate = function () {
    var dialogOpts = {
      id: "activeConfirmModel",
      header: messageService.get("create.facility.activateFacility"),
      body: messageService.get("create.facility.setFacilityActive")
    };
    OpenLmisDialog.newDialog(dialogOpts, $scope.activateFacilityCallBack, $dialog, messageService);
  };

  $scope.activateFacilityCallBack = function (result) {
    $scope.enableFacility(result);
  };

  function updateProgramsToDisplay() {
    $scope.facility.supportedPrograms = $scope.facility.supportedPrograms || [];
    var supportedProgramIds = _.pluck(_.pluck($scope.facility.supportedPrograms, 'program'), "id");
    $scope.programsToDisplay = _.reject($scope.programs, function (supportedProgram) {
      return _.contains(supportedProgramIds, supportedProgram.id);
    });
    $scope.programSupportedMessage = ($scope.programsToDisplay.length) ? 'label.select.program.supported' : 'label.no.programs.left';
  }
}

FacilityController.resolve = {
  facilityReferenceData: function ($q, $timeout, FacilityReferenceData) {
    var deferred = $q.defer();
    $timeout(function () {
      FacilityReferenceData.get({}, function (data) {
        deferred.resolve(data);
      }, {});
    }, 100);
    return deferred.promise;
  },

  facility: function ($q, $timeout, Facility, $route) {
    if ($route.current.params.facilityId === undefined) return undefined;

    var deferred = $q.defer();
    var facilityId = $route.current.params.facilityId;

    $timeout(function () {
      Facility.get({id: facilityId}, function (data) {
        deferred.resolve(data.facility);
      }, {});
    }, 100);
    return deferred.promise;
  }
};



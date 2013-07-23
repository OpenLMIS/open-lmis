/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
      populateFlags($scope);
    } else {
      $scope.facility = {};
      updateProgramsToDisplay();
      $scope.facility.dataReportable = "true";
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
    $scope.programProductsISAModal = true;
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

    })

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
        populateFlags($scope);
        $location.path('');
      }, function (error) {
        $scope.showError = "true";
        $scope.message = "";
        $scope.error = error.error;
        $scope.errorProgram = error.program;
      })
    };

    var errorFn = function (data) {
      $scope.showError = "true";
      $scope.message = "";
      $scope.error = data.data.error;
    };

    if (!$scope.isEdit) {
      Facility.save({}, $scope.facility, facilitySaveCallback, errorFn);
    } else {
      Facility.update({id: $scope.facility.id}, $scope.facility, facilitySaveCallback, errorFn);
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
    if(getProgramById(program.program.id).push) {
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
      $scope.removeSupportedProgram()
    }
    $scope.selectedSupportedProgram = undefined;
  }


  $scope.removeSupportedProgram = function () {
    if ($scope.facility.dataReportable == 'false') return;
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
    populateFlags($scope);
  };

  var errorFunc = function (data) {
    $scope.showError = "true";
    $scope.message = "";
    $scope.error = data.data.error;
  };

  $scope.restoreFacility = function (active) {
    $scope.activeConfirmModal = false;
    $scope.facility.active = active;
    Facility.restore({id: $scope.facility.id, active: active}, successFunc, errorFunc);
  };

  $scope.deleteFacilityCallBack = function (result) {
    if (!result) return;
    Facility.remove({id: $scope.facility.id}, {}, successFunc, errorFunc);
  };

  $scope.showConfirmFacilityDeleteWindow = function () {
    var dialogOpts = {
      id: "deleteFacilityDialog",
      header: messageService.get('delete.facility.header'),
      body: messageService.get('delete.facility.confirm', $scope.originalFacilityName, $scope.originalFacilityCode)
    };
    OpenLmisDialog.newDialog(dialogOpts, $scope.deleteFacilityCallBack, $dialog, messageService);
  };

  $scope.showConfirmFacilityRestore = function () {
    var dialogOpts = {
      id: "restoreConfirmModal",
      header: messageService.get("create.facility.restoreFacility"),
      body: "'{0}' / '{1}' will be restored to the system.".format($scope.originalFacilityName, $scope.originalFacilityCode)
    };
    OpenLmisDialog.newDialog(dialogOpts, $scope.restoreFacilityCallBack, $dialog, messageService);
  };

  $scope.restoreFacilityCallBack = function (result) {
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
    $scope.restoreFacility(result);
  };

  function updateProgramsToDisplay() {
    $scope.facility.supportedPrograms = $scope.facility.supportedPrograms || [];
    var supportedProgramIds = _.pluck(_.pluck($scope.facility.supportedPrograms, 'program'), "id");
    $scope.programsToDisplay = _.reject($scope.programs, function (supportedProgram) {
      return _.contains(supportedProgramIds, supportedProgram.id)
    });
    $scope.programSupportedMessage = ($scope.programsToDisplay.length) ? 'label.select.program.supported' : 'label.no.programs.left';
  }

}

var populateFlags = function ($scope) {
  $(['suppliesOthers', 'sdp', 'hasElectricity', 'online', 'hasElectronicScc', 'hasElectronicDar', 'active', 'dataReportable']).each(function (index, field) {
    var value = $scope.facility[field];
    $scope.facility[field] = (value == null) ? "" : value.toString();
  });
};

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
    if ($route.current.params.facilityId == undefined) return undefined;

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



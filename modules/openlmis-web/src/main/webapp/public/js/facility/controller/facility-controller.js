/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function FacilityController($scope, facilityReferenceData, $routeParams, $http, facility, Facility, $location, $dialog) {

  function getFacilityWithDateObjects(facility) {
    facility.goLiveDate = new Date(facility.goLiveDate);
    facility.goDownDate = new Date(facility.goDownDate);
    angular.forEach(facility.supportedPrograms, function (supportedProgram) {
      supportedProgram.startDate = new Date(supportedProgram.startDate);
    });
    return facility;
  }

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
  }

  $scope.message = "";

  $scope.saveFacility = function () {
    if ($scope.facilityForm.$error.pattern || $scope.facilityForm.$error.required) {
      $scope.showError = "true";
      $scope.error = "There are some errors in the form. Please resolve them.";
      $scope.message = "";
      return;
    }

    var successFn = function (data) {
      $scope.showError = "true";
      $scope.error = "";
      $scope.$parent.message = data.success;
      $scope.facility = getFacilityWithDateObjects(data.facility);
      $scope.$parent.facilityId = $scope.facility.id;
      populateFlags($scope);
      $location.path('');
    };

    var errorFn = function (data) {
      $scope.showError = "true";
      $scope.message = "";
      $scope.error = data.data.error;
    };

    if (!$scope.isEdit) {
      Facility.save({}, $scope.facility, successFn, errorFn);
    } else {
      Facility.update({id:$scope.facility.id}, $scope.facility, successFn, errorFn);
    }
  };


  var putFacilityRequest = function (requestUrl) {
    $http.put(requestUrl, $scope.facility)
      .success(function (data) {
        $scope.showError = "true";
        $scope.error = "";
        $scope.message = data.success;
        $scope.facility = getFacilityWithDateObjects(data.facility);
        $scope.originalFacilityCode = data.facility.code;
        $scope.originalFacilityName = data.facility.name;
        populateFlags($scope);
      }).error(function (data) {
        $scope.showError = "true";
        $scope.message = "";
        $scope.error = data.error;
        $scope.facility = facility;
        $scope.originalFacilityCode = data.facility.code;
        $scope.originalFacilityName = data.facility.name;
        populateFlags($scope);
      });
  };

  $scope.restoreFacility = function (active) {
    $scope.activeConfirmModal = false;
    $scope.facility.active = active;
    putFacilityRequest('/facility/update/restore.json');
  };

  $scope.blurDateFields = function () {
    angular.element("input[ui-date]").blur();
  };

  $scope.addSupportedProgram = function (supportedProgram) {
    if (supportedProgram.active && !supportedProgram.editedStartDate) {
      $scope.showDateNotEnteredError = true;
      return;
    }
    $scope.facility.supportedPrograms.push(supportedProgram);
    $scope.showDateNotEnteredError = false;
    $scope.supportedProgram = undefined;
    updateProgramsToDisplay();
  };

  $scope.showConfirmDateChangeWindow = function (program) {
    window.program = program;
    var dialogOpts = {
      id:"dateChangeConfirmModal",
      header:"Set Program Start Date",
      body:"Facility Staff will submit back-due R&Rs for this program, starting from this date."
    };
    OpenLmisDialog.new(dialogOpts, $scope.dateChangeCallback, $dialog);
  };

  $scope.dateChangeCallback = function (result) {
    if (result) {
      window.program.startDate = window.program.editedStartDate;
    } else {
      window.program.editedStartDate = window.program.startDate;
    }
  };

  $scope.removeSupportedProgram = function (supportedProgram) {
    if ($scope.facility.dataReportable == 'false') return;
    $scope.facility.supportedPrograms = _.without($scope.facility.supportedPrograms, supportedProgram);
    updateProgramsToDisplay();
  };

  $scope.getProgramNameById = function (programId) {
    return (_.findWhere($scope.programs, {'id':programId})).name;
  };

  $scope.deleteFacilityCallBack = function (result) {
    if (!result) return;
    putFacilityRequest('/facility/update/delete.json');
  };

  $scope.showConfirmFacilityDeleteWindow = function () {
    var dialogOpts = {
      id:"deleteFacilityDialog",
      header:"Delete facility",
      body:"'{0}' / '{1}' will be deleted from the system.".format($scope.originalFacilityName, $scope.originalFacilityCode)
    };
    OpenLmisDialog.new(dialogOpts, $scope.deleteFacilityCallBack, $dialog);
  };

  function updateProgramsToDisplay() {
    $scope.facility.supportedPrograms = $scope.facility.supportedPrograms || [];
    var supportedProgramIds = _.pluck(_.pluck($scope.facility.supportedPrograms, 'program'), "id");
    $scope.programsToDisplay = _.reject($scope.programs, function (supportedProgram) {
      return _.contains(supportedProgramIds, supportedProgram.id)
    });
    $scope.programSupportedMessage = ($scope.programsToDisplay.length) ? '--Select Program Supported--' : '--No Program Left--';
  }

  initialize();
}

var populateFlags = function ($scope) {
  $(['suppliesOthers', 'sdp', 'hasElectricity', 'online', 'hasElectronicScc', 'hasElectronicDar', 'active', 'dataReportable']).each(function (index, field) {
    var value = $scope.facility[field];
    $scope.facility[field] = (value == null) ? "" : value.toString();
  });
};

FacilityController.resolve = {

  facilityReferenceData:function ($q, $timeout, FacilityReferenceData) {
    var deferred = $q.defer();
    $timeout(function () {
      FacilityReferenceData.get({}, function (data) {
        deferred.resolve(data);
      }, {});
    }, 100);
    return deferred.promise;
  },

  facility:function ($q, $timeout, Facility, $route) {
    if ($route.current.params.facilityId == undefined) return undefined;

    var deferred = $q.defer();
    var facilityId = $route.current.params.facilityId;

    $timeout(function () {
      Facility.get({id:facilityId}, function (data) {
        deferred.resolve(data.facility);
      }, {});
    }, 100);
    return deferred.promise;
  }
};



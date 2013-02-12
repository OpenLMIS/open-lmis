function FacilityController($scope, facilityReferenceData, $routeParams, $http, facility, Facility, $rootScope, $location) {

  function initialize() {
    $scope.facilityTypes = facilityReferenceData.facilityTypes;
    $scope.geographicZones = facilityReferenceData.geographicZones;
    $scope.facilityOperators = facilityReferenceData.facilityOperators;
    $scope.programs = facilityReferenceData.programs;
    if ($routeParams.facilityId) {
      $scope.facility = facility;
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
      $scope.facility = data.facility;
      $scope.$parent.facilityId = $scope.facility.id;
      populateFlags($scope);
      $location.path('');
    };

    var errorFn = function (data) {
      $scope.showError = "true";
      $scope.message = "";
      $scope.error = data.data.error;
    };

    if(!$scope.isEdit){
      Facility.save({}, $scope.facility, successFn, errorFn);
    }else{
      Facility.update({id: $scope.facility.id}, $scope.facility, successFn, errorFn);
    }
  };


  var putFacilityRequest = function (requestUrl) {
    $http.put(requestUrl, $scope.facility).success(function (data) {
      $scope.showError = "true";
      $scope.error = "";
      $scope.message = data.success;
      $scope.facility = data.facility;
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
  $scope.deleteFacility = function () {
    $scope.deleteConfirmModal = false;
    putFacilityRequest('/facility/update/delete.json');

  };

  $scope.restoreFacility = function (active) {
    $scope.activeConfirmModal = false;
    $scope.facility.active = active;
    putFacilityRequest('/facility/update/restore.json');
  };

  $scope.blurDateFields = function () {
    angular.element("input[ui-date]").blur();
  };

  $scope.addSupportedProgram = function() {
    if($scope.supportedProgram.active && !$scope.supportedProgram.startDate) {
      $scope.showDateNotEnteredError = {};
      return;
    }
    var supportedProgram = {};
    angular.copy($scope.supportedProgram, supportedProgram);
    $scope.facility.supportedPrograms.push(supportedProgram);
    $scope.showDateNotEnteredError = undefined;
    $scope.supportedProgram = undefined;
    updateProgramsToDisplay();
  };

  $scope.editStartDate = function(program) {
    window.program = program;
    $scope.dateChangeConfirmModal=true;
  };

  $scope.setNewStartDate = function() {
    window.program.startDate = window.program.editedStartDate;
    $scope.dateChangeConfirmModal = false;
  };

  $scope.resetOldStartDate = function() {
    window.program.editedStartDate = window.program.startDate;
    $scope.dateChangeConfirmModal = false;
  };

  $scope.removeSupportedProgram = function(supportedProgram) {
    if($scope.facility.dataReportable == 'false') return;
    $scope.facility.supportedPrograms = _.without($scope.facility.supportedPrograms, supportedProgram);
    updateProgramsToDisplay();
  };

  $scope.getProgramNameById = function(programId) {
    return (_.findWhere($scope.programs, {'id' : programId})).name;
  };

  function updateProgramsToDisplay() {
    $scope.facility.supportedPrograms = (!$scope.facility.supportedPrograms) ? [] : $scope.facility.supportedPrograms;
    var supportedProgramIds = _.pluck(_.pluck($scope.facility.supportedPrograms, 'program'),"id");
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



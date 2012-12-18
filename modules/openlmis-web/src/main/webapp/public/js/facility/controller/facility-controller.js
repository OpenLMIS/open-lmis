function FacilityController($scope, facilityReferenceData, $routeParams, $http, facility) {

    $scope.facilityTypes = facilityReferenceData.facilityTypes;
    $scope.geographicZones = facilityReferenceData.geographicZones;
    $scope.facilityOperators = facilityReferenceData.facilityOperators;
    $scope.programs = facilityReferenceData.programs;

//TODO Need a more elegant solution
        if ($routeParams.facilityId) {
                $scope.facility = facility;
                $scope.originalFacilityCode = facility.code;
                $scope.originalFacilityName = facility.name;
                populateFlags($scope);
                //TODO Need a more elegant solution
                var foo = [];
                $.each($scope.facility.supportedPrograms, function (index, supportedProgram) {
                    $.each($scope.programs, function (index, program) {
                        if (supportedProgram.code == program.code) {
                            program.active = supportedProgram.active;
                            foo.push(program);
                        }
                    })
                });
                $scope.facility.supportedPrograms = foo;
        } else {
            $scope.facility = {};
            $scope.facility.dataReportable = "true";
        }


  $scope.saveFacility = function () {
    if ($scope.facilityForm.$error.pattern || $scope.facilityForm.$error.required) {
      $scope.showError = "true";
      $scope.error = "There are some errors in the form. Please resolve them.";
      $scope.message = "";
    }
    else {
      $http.post('/admin/facility.json', $scope.facility).success(function (data) {
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
        });
    }
  };
  var postFacilityRequest = function (requestUrl) {

    $http.post(requestUrl, $scope.facility).success(function (data) {
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
    postFacilityRequest('/admin/facility/update/delete.json');
  };

  $scope.restoreFacility = function (active) {
    $scope.facility.active = active;
    postFacilityRequest('/admin/facility/update/restore.json');
  }

  $scope.fixToolBar = function() {
    var toolbarWidth = window.innerWidth - 279;
    angular.element("#action_buttons").css("width", toolbarWidth + "px");
  }();
}

var populateFlags = function ($scope) {
  $(['suppliesOthers', 'sdp', 'hasElectricity', 'online', 'hasElectronicScc', 'hasElectronicDar', 'active', 'dataReportable']).each(function (index, field) {
    var value = $scope.facility[field];
    $scope.facility[field] = (value == null) ? "" : value.toString();
  });
};


FacilityController.resolve = {

  facilityReferenceData :function ($q, $timeout, FacilityReferenceData) {
    var deferred = $q.defer();
    $timeout(function () {
      FacilityReferenceData.get({}, function (data) {
        deferred.resolve(data);
      }, {});
    }, 100);
    return deferred.promise;
  },

  facility : function ($q, $timeout, Facility, $route) {
    if($route.current.params.facilityId == undefined) return undefined;

    var deferred = $q.defer();
    var facilityId = $route.current.params.facilityId;

    $timeout(function () {
      Facility.get({id : facilityId}, function(data) {
        deferred.resolve(data.facility);
      }, {});
    }, 100);
    return deferred.promise;
  }
};



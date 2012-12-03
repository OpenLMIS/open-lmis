function FacilityController($scope, FacilityReferenceData,$routeParams,$http, Facility) {

  if($routeParams.facilityId){
    Facility.get({id:$routeParams.facilityId}, function(data) {
      $scope.facility = data.facility;
    }, {});
  } else {
    $scope.facility = {};
    $scope.facility.dataReportable = "true";
  }


  FacilityReferenceData.get({} , function (data) {
    $scope.facilityTypes = data.facilityTypes;
    $scope.programs = data.programs;
    $scope.geographicZones = data.geographicZones;
    $scope.facilityOperators = data.facilityOperators;
  }, {});

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
      }).error(function (data) {
          $scope.showError = "true";
          $scope.message = "";
          $scope.error = data.error;
        });
    }
  };

}
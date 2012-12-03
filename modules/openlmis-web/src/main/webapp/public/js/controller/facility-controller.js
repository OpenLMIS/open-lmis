function FacilityController($scope, FacilityReferenceData,$http) {

  setUpFacilityDefaults= function(){
    $scope.facility = {};
    $scope.facility.suppliesOthers = "true";
    $scope.facility.active = "true";
    $scope.facility.sdp = "true";
    $scope.facility.dataReportable = "true";
    $scope.facility.hasElectricity = "true";
    $scope.facility.online = "true";
    $scope.facility.hasElectronicScc = "true";
    $scope.facility.hasElectronicDar = "true";
    $scope.facility.satellite = "false";
  }();


  FacilityReferenceData.get({} , function (data) {
    $scope.facilityTypes = data.facilityTypes;
    $scope.programs = data.programs;
    $scope.geographicZones = data.geographicZones;
    $scope.facilityOperators = data.facilityOperators;
  }, {});

  $scope.saveFacility = function () {
    if ($scope.facilityForm.$error.pattern || $scope.facilityForm.$error.required) {
      $scope.error = "There are some errors in the form. Please fix them";
      $scope.message = "";
    }
    else {
      $http.post('/admin/facility.json', $scope.facility).success(function (data) {
        $scope.error = "";
        $scope.message = data.success;
      }).error(function (data) {
          $scope.message = "";
          $scope.error = data.error;
        });
    }
  };

}
function FacilityController($scope, FacilityReferenceData,$http) {

  setUpFacilityDefaults= function(){
    $scope.facility = {};
    $scope.facility.suppliesOthers = "true";
    $scope.facility.active = "true";
    $scope.facility.sdp = "true";
    $scope.facility.doNotDisplay = "false";
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
    if ($scope.facilityForm.$error.pattern) {
      $scope.error = "Some field holds incorrect value. Check above";
      $scope.message = "";
    }
    else {
      $http.post('/admin/facility.json', $scope.facility).success(function () {
        $scope.error = "";
        $scope.message = "Saved successfully";
      }).error(function () {
          $scope.message = "";
          $scope.error = "Save failed";
        });
    }
  };

  $scope.setSatelliteFlag = function(isSatellite) {
    if(isSatellite=="true"){
      $scope.satelliteFlag = "show";
    }
    else
    {
      $scope.satelliteFlag = null;
    }
  }

}
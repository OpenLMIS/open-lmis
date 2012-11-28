function FacilityController($scope, FacilityReferenceData,$http) {

  FacilityReferenceData.get({} , function (data) {
    $scope.facilityTypes = data.facilityTypes;
    $scope.programs = data.programs;
    $scope.geographicZones = data.geographicZones;
    $scope.facilityOperators = data.facilityOperators;
  }, {});

  $scope.saveFacility = function(facility){

    $http.post('/admin/facility.json',facility).success(function() {
        $scope.error = "";
        $scope.message = "Saved successfully";
    }).error(function(){
        $scope.message = "";
      $scope.error = "Save failed";
    });

  }

}
function FacilityController($scope, FacilityReferenceData) {

  FacilityReferenceData.get({} , function (data) {
    $scope.facilityTypes = data.facilityTypes;
    $scope.programs = data.programs;
    $scope.geographicZones = data.geographicZones;
    $scope.facilityOperators = data.facilityOperators;
  }, {})

}
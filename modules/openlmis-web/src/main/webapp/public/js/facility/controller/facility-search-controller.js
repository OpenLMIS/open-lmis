function FacilitySearchController($scope, AllFacilities, $location) {

  AllFacilities.get({}, function(data) {
    $scope.facilityList = data.facilityList;
  }, {});


  $scope.editFacility = function(id){
    $location.path('edit/'+id);
  }
}
function ListFacilitiesController($scope, FacilityList, $http) {
    $http.get('http://localhost:9091/public/jsons/facilitylist.json').success(function(data) {
        $scope.facilitylist = data.facilitylist;
    });


}

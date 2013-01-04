function FacilitySearchController($scope, AllFacilities, $location) {

    AllFacilities.get({}, function (data) {
        $scope.facilityList = data.facilityList;
    }, {});

    $scope.editFacility = function (id) {
        $location.path('edit/' + id);
    };


    $scope.filterFacilitiesByNameOrCode = function (query) {
        var filteredFacilities = [];
        query = query || "";

        angular.forEach($scope.facilityList, function (facility) {
            if (facility.name.toLowerCase().indexOf(query.toLowerCase()) >= 0 || facility.code.toLowerCase().indexOf(query.toLowerCase()) >= 0) {
                filteredFacilities.push(facility);
            }
        });
        $scope.resultCount = filteredFacilities.length;
        return filteredFacilities;
    };

    $scope.clearSearch = function () {
        $scope.query = "";
        $scope.resultCount = 0;
        angular.element("#searchFacility").focus();
    };
}


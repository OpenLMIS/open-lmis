function RnrController($scope, Facility) {
    Facility.get({}, function (data) {   //success
        $scope.facilities = data.facilityList;
    }, {});
}

function FacilityController($scope, FacilitySupportedPrograms) {
FacilitySupportedPrograms.get({facility:$scope.facility}, function (data) {
    $scope.programsForFacility = data.programList;
}, {});

}
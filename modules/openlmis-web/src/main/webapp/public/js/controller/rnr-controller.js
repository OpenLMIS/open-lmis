var scope;
function RnrController($scope, Facility, FacilitySupportedPrograms) {
    Facility.get({}, function (data) {   //success
        $scope.facilities = data.facilityList;
    }, {});

    $scope.loadPrograms = function() {
        FacilitySupportedPrograms.get({facility:$scope.facility}, function (data) {
            $scope.programsForFacility = data.programList;
        }, {});
    }
}

function HeaderController($scope) {
}
function RnrController($scope, Facility, FacilitySupportedPrograms) {
    Facility.get({}, function (data) {
            $scope.facilities = data.facilityList;
        }, {}
    );

    $scope.loadPrograms = function ($scope) {
        FacilitySupportedPrograms.get({facility:$scope.facility}, function (data) {
            $scope.programsForFacility = data.programList;
        }, {});
    }
}

function RnrHeaderController($scope, RequisitionHeader, $location) {
    RequisitionHeader.get({code:$scope.facility}, function (data) {
        $scope.header = data.requisitionHeader;
    }, function () {
        $location.path("new-rnr");
    });
}
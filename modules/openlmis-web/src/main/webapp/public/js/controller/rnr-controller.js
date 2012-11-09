var scope;
function RnrController($scope, Facility, FacilitySupportedPrograms) {
    Facility.get({}, function (data) {
        $scope.facilities = data.facilityList;
    }, {});

    $scope.loadPrograms = function () {
        FacilitySupportedPrograms.get({facility:$scope.facility}, function (data) {
            $scope.programsForFacility = data.programList;
        }, {});
    }
}

function HeaderController($scope, RequisitionHeader) {
    RequisitionHeader.get({code:$scope.facility}, function (data) {
        $scope.header = data.requisitionHeader;
    }, {});
}
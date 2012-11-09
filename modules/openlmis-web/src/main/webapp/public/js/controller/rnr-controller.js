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
    $scope.saveProgram = function () {
        $.each($scope.programsForFacility, function (i, program) {
            if (program.id === $scope.program) {
                $scope.selectedProgramName = program.name;
            }
        });
    }
}

function HeaderController($scope, RequisitionHeader) {
    RequisitionHeader.get({code:$scope.facility}, function (data) {
        $scope.header = data.requisitionHeader;
    }, {});
}
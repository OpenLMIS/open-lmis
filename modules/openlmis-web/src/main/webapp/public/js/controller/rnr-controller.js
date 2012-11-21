function InitiateRnrController($scope, Facility, FacilitySupportedPrograms, $location) {
    Facility.get({}, function (data) {
            $scope.facilities = data.facilityList;
        }, {}
    );

    $scope.loadPrograms = function ($scope) {
        FacilitySupportedPrograms.get({facility:$scope.facility}, function (data) {
            $scope.program = null;
            $scope.programsForFacility = data.programList;
        }, {});
    };

    var validate = function ($scope) {
        return $scope.program;
    };

    $scope.getRnrHeader = function ($scope) {
        if (validate($scope)) {
            $location.path('create-rnr');
        }
        else {
            alert('You need to select Facility and program for facility to proceed');
        }
    }
}

function CreateRnrController($scope, RequisitionHeader, ProgramRnRColumnList, $location) {

    RequisitionHeader.get({code:$scope.facility}, function (data) {
        $scope.header = data.requisitionHeader;
    }, function () {
        $location.path("init-rnr");
    });

    ProgramRnRColumnList.get({programCode:$scope.program.code}, function (data) {   //success
        $scope.programRnRColumnList = data.rnrColumnList;
    }, function () {
        $location.path('init-rnr');
    });
}
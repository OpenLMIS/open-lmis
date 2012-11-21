function InitiateRnrController($http, $scope, Facility, FacilitySupportedPrograms, $location) {
    Facility.get({}, function (data) {
            $scope.facilities = data.facilityList;
        }, {}
    );

    $scope.loadPrograms = function () {
        FacilitySupportedPrograms.get({facility:$scope.facility}, function (data) {
            $scope.program = null;
            $scope.programsForFacility = data.programList;
        }, {});
    };

    $scope.getRnrHeader = function () {
        if (validate()) {
            $scope.error = "";
            initRnr();
        }
        else {
            $scope.error = "Please select Facility and program for facility to proceed";
        }
    };

    var validate = function () {
        return $scope.program;
    };

    var initRnr = function () {
        $http.post('/logistics/rnr/' + $scope.facility + '/' + $scope.program.code + '/init.json', {}).success(function () {
            $scope.error = "";
            $location.path('create-rnr');
        }).error(function () {
                $scope.error = "Rnr initialization failed!";
                $scope.message = "";
            });
    };
}

function CreateRnrController($scope, RequisitionHeader, ProgramRnRColumnList, $location) {

    RequisitionHeader.get({code:$scope.facility}, function (data) {
        $scope.header = data.requisitionHeader;
    }, function () {
        $location.path("init-rnr");
    });

    ProgramRnRColumnList.get({programCode:$scope.program.code}, function (data) {
        $scope.programRnRColumnList = data.rnrColumnList;
    }, function () {
        $location.path('init-rnr');
    });

}
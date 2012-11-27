function InitiateRnrController($http, $scope, Facility, FacilitySupportedPrograms, $location) {
    Facility.get({}, function (data) {
            $scope.facilities = data.facilityList;
        }, {}
    );

    $scope.loadPrograms = function () {
        if ($scope.$parent.facility) {
            FacilitySupportedPrograms.get({facilityCode:$scope.facility}, function (data) {
                $scope.$parent.programsForFacility = data.programList;
            }, {});
        } else {
            $scope.$parent.programsForFacility = null;
        }
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
        return $scope.$parent.program;
    };

    var initRnr = function () {
        $http.post('/logistics/rnr/' + $scope.facility + '/' + $scope.program.code + '/init.json', {}).success(function (data) {
            $scope.error = "";
            $scope.$parent.rnr = data.rnr;
            $location.path('create-rnr');
        }).error(function () {
                $scope.error = "Rnr initialization failed!";
                $scope.message = "";
            });
    };
}

function CreateRnrController($scope, RequisitionHeader, ProgramRnRColumnList, $location) {
    RequisitionHeader.get({code:$scope.$parent.facility}, function (data) {
        $scope.header = data.requisitionHeader;
    }, function () {
        $location.path("init-rnr");
    });

    ProgramRnRColumnList.get({programCode:$scope.$parent.program.code}, function (data) {
        if (validate(data)) {
            $scope.$parent.error = "";
            $scope.programRnRColumnList = data.rnrColumnList;
        } else {
            $scope.$parent.error = "Please contact Admin to define R&R template for this program";
            $location.path('init-rnr');
        }
    }, function () {
        $location.path('init-rnr');
    });


    var validate = function (data) {
        return data.rnrColumnList.length > 0 ? true : false;
    }

}
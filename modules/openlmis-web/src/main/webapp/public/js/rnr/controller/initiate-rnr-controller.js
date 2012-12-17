function InitiateRnrController($http, $scope, facilities, UserSupportedProgramInFacilityForAnOperation, $location) {

    $scope.programOptionMsg = "--choose program--";
    $scope.facilities = facilities;
    $scope.facilityOptionMsg = "--choose facility--";
    if ($scope.facilities == null || $scope.facilities.length == 0) {
        $scope.facilityOptionMsg = "--none assigned--";
    }

    $scope.loadPrograms = function () {
        if ($scope.$parent.facility) {
            UserSupportedProgramInFacilityForAnOperation.get({facilityId:$scope.$parent.facility}, function (data) {
                $scope.$parent.programsForFacility = data.programList;
                $scope.programOptionMsg = "--choose program--";
                if ($scope.$parent.programsForFacility == null || $scope.$parent.programsForFacility.length == 0) {
                    $scope.programOptionMsg = "--none assigned--";
                }
            }, {});
        } else {
            $scope.$parent.program = null;
            $scope.$parent.programsForFacility = null;
            $scope.programOptionMsg = "--choose program--";
        }
    };

    $scope.initRnr = function () {
        if (validate()) {
            $http.post('/logistics/rnr/' + encodeURIComponent($scope.facility) + '/' + encodeURIComponent($scope.program.code) + '/init.json', {}
            ).success(function (data) {
                    $scope.error = "";
                    $scope.$parent.rnr = data.rnr;
                    $location.path('create-rnr');
                }
            ).error(function () {
                    $scope.error = "Rnr initialization failed!";
                    $scope.message = "";
                });
        } else {
            $scope.error = "Please select Facility and program for facility to proceed";
        }
    };

    var validate = function () {
        return $scope.$parent.program;
    };
}

InitiateRnrController.resolve = {
    facilities:function ($q, $timeout, UserFacilityList) {
        var deferred = $q.defer();
        $timeout(function () {
            UserFacilityList.get({}, function (data) {
                    deferred.resolve(data.facilityList);
                }, {}
            );
        }, 100);

        return deferred.promise;
    }
}
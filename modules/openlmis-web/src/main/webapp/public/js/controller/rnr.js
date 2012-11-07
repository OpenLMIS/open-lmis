function RnrController($scope, Facility, Program, $http) {
    Facility.get({}, function (data) {   //success
        $scope.facilities = data.facilityList;
    }, {});

    Program.get({}, function (data) {   //success
        $scope.programs = data.programList;
    }, {});
}

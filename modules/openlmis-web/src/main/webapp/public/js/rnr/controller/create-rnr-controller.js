function CreateRnrController($scope, RequisitionHeader, ProgramRnRColumnList, $location, $http) {

    RequisitionHeader.get({facilityId:$scope.$parent.facility}, function (data) {
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
        return (data.rnrColumnList.length > 0);
    };

    $scope.saveRnr = function () {
        if ($scope.saveRnrForm.$error.rnrError != undefined && $scope.saveRnrForm.$error.rnrError != false && $scope.saveRnrForm.$error.rnrError.length > 0) {
            $scope.error = "Please correct errors before saving.";
            $scope.message = "";
            return;
        }
        $http.post('/logistics/rnr/' + $scope.$parent.rnr.id + '/save.json', $scope.$parent.rnr).success(function (data) {
            $scope.message = "R&R saved successfully!";
            $scope.error = "";
        });
    };

    $scope.fillCalculatedRnrColumns = function (lineItem) {
        rnrModule.fill(lineItem, $scope.programRnRColumnList);
    };

    $scope.getId = function (prefix, parent) {
        return prefix + "_" + parent.$parent.$parent.$index;
    };
}
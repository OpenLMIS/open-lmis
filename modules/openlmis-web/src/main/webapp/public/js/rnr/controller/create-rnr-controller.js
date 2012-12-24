function CreateRnrController($scope, ReferenceData, RequisitionHeader, ProgramRnRColumnList, localStorageService, $location, $http) {
    RequisitionHeader.get({facilityId:$scope.$parent.facility}, function (data) {
        $scope.header = data.requisitionHeader;
        $scope.getCurrency();
    }, function () {
        $location.path("init-rnr");
    });

    ProgramRnRColumnList.get({programCode:$scope.$parent.program.code}, function (data) {
        function resetFullSupplyItemsCostIfNull(rnr){
            if(rnr == null) return;
            if(rnr.fullSupplyItemsSubmittedCost == null)
               rnr.fullSupplyItemsSubmittedCost = 0;
        }

        function resetTotalSubmittedCostIfNull(rnr){
            if(rnr == null) return;
            if(rnr.totalSubmittedCost == null)
               rnr.totalSubmittedCost = 0;
        }

        if (validate(data)) {
            $scope.$parent.error = "";
            $scope.programRnRColumnList = data.rnrColumnList;
            resetFullSupplyItemsCostIfNull($scope.$parent.rnr);
            resetTotalSubmittedCostIfNull($scope.$parent.rnr);
        } else {
            $scope.$parent.error = "Please contact Admin to define R&R template for this program";
            $location.path('init-rnr');
        }
    }, function () {
        $location.path('init-rnr');
    });

    // TODO : is this required?
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

    $scope.getCurrency = function () {
           ReferenceData.get({}, function (data) {
                $scope.currency = data.responseData;
           }, {});
    };

    $scope.fillCalculatedRnrColumns = function (lineItem, rnr, data) {
        rnrModule.fill(lineItem, $scope.programRnRColumnList, rnr);
    };

    $scope.getId = function (prefix, parent) {
        return prefix + "_" + parent.$parent.$parent.$index;
    };

    $scope.hide = function () {
     return "";
    }

    $scope.show = function(data) {
        if(data != 0 && (data == undefined || data == null || data == false)){
            return "";
        }
        return "defined";
    }
}
function SingleFacilityReportController($scope, $filter, $controller, ProductReportService) {
    $controller('BaseProductReportController', {$scope: $scope});

    $scope.$on('$viewContentLoaded', function () {
        $scope.loadHealthFacilities();
    });

    $scope.loadReport = function () {
        var params = {endTime: $filter('date')($scope.reportParams.endTime, "yyyy-MM-dd HH:mm:ss")};
        params.facilityId = $scope.reportParams.facilityId;

        if (validateFacility()) {
            ProductReportService.loadFacilityReport().get(params, function (data) {
                $scope.reportData = data.products;
            });
        }
    };

    function validateFacility() {
        $scope.invalid = !$scope.reportParams.facilityId;
        return !$scope.invalid;
    }

}
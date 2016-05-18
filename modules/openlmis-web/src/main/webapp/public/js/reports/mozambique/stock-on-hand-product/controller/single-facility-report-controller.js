function SingleFacilityReportController($scope, $filter, $controller, ProductReportService, FeatureToggleService) {
    $controller('BaseProductReportController', {$scope: $scope});

    $scope.$on('$viewContentLoaded', function () {

        FeatureToggleService.get({key: 'view.stock.movement'}, function (result) {
          $scope.viewStockMovementToggle = result.key;
        });

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
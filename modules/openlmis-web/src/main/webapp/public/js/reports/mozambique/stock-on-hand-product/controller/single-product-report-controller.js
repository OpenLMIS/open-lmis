function SingleProductReportController($scope, $filter, $controller, ProductReportService) {
    $controller('BaseProductReportController', {$scope: $scope});

    $scope.$on('$viewContentLoaded', function () {
        $scope.loadProducts();
    });

    $scope.loadReport = function(){
        var params = {endTime: $filter('date')($scope.reportParams.endTime, "yyyy-MM-dd HH:mm:ss")};
        params.productId = $scope.reportParams.productId;
        params.geographicZoneId = $scope.reportParams.districtId || $scope.reportParams.provinceId;

        if (validateProduct()) {
            ProductReportService.loadProductReport().get(params, function (data) {
                $scope.reportData = data.products;
            });
        }
    }

    function validateProduct() {
        $scope.invalid = !$scope.reportParams.productId;
        return !$scope.invalid;
    }

}
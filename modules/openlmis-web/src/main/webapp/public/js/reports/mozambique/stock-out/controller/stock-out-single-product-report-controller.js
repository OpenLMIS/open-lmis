function StockOutSingleProductReportController($scope, $filter, $controller, $http, CubesGenerateUrlService, messageService, $dialog, DateFormatService) {
    $controller('BaseProductReportController', {$scope: $scope});

    $scope.getTimeRange =function(dateRange){
        $scope.reportParams.startTime = dateRange.startTime;
        $scope.reportParams.endTime = dateRange.endTime;
    };

    function showDateRangeInvalidWarningDialog() {
        var options = {
            id: "chooseDateAlertDialog",
            header: "title.alert",
            body: "dialog.date.range.invalid.warning"
        };
        MozambiqueDialog.newDialog(options, function () {
        }, $dialog);
    }

    $scope.$on('$viewContentLoaded', function () {
        $scope.loadProducts();
    });

    $scope.loadReport = function () {
        if (isInvalidDateRange()) {
            showDateRangeInvalidWarningDialog();
            return;
        }

        getStockOutDataFromCubes();
    };

    function isInvalidDateRange() {
        return $scope.reportParams.startTime > $scope.reportParams.endTime;
    }

    function getStockReportRequestParam() {
        var params = {};
        params.startTime = $filter('date')($scope.reportParams.startTime, "yyyy,MM,dd");
        params.endTime = $filter('date')($scope.reportParams.endTime, "yyyy,MM,dd");
        params.drugCode = $scope.reportParams.productCode;
        return params;
    }

    function generateCutParams(stockReportParams) {
        var cutsParams = [{
            dimension: "overlapped_date",
            values: [stockReportParams.startTime + "-" + stockReportParams.endTime]
        }];

        cutsParams.push({dimension:"drug", values:[stockReportParams.drugCode]});
        return cutsParams;
    }

    function getStockOutDataFromCubes() {
        if (!validateProduct()){
            return;
        }

        $http.get(CubesGenerateUrlService.generateFactsUrl('vw_stockouts', generateCutParams(getStockReportRequestParam())))
            .success(function (data) {
                $scope.reportData = data;
        });
    }

    function validateProduct() {
        $scope.invalid = !$scope.reportParams.productCode;
        return !$scope.invalid;
    }

}
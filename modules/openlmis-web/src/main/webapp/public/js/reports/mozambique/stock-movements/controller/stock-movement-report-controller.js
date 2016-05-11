function StockMovementReportController($scope, $routeParams, FacilityCode, $http, CubesGenerateUrlService) {

    $scope.loadFacilityAndStockMovements = function() {
        FacilityCode.get({
            code: $routeParams.facilityCode
        }, function(data) {
            $scope.facilityName = data.facility.name;
            $scope.district = data.facility.geographicZone.name;
            $scope.province = data.facility.geographicZone.parent.name;

            loadStockMovements();
        })
    };

    var loadStockMovements = function () {
        var cutsParams = [];
        cutsParams.push({dimension: "facility", values: [$scope.facilityName]});
        cutsParams.push({dimension: "product", values: [$routeParams.productCode]});

        $http.get(CubesGenerateUrlService.generateFactsUrl('vw_stock_movements', cutsParams)).success(function (data) {
            $scope.stockMovements = [];
            _.each(data, function (item) {
                setQuantityByType(item);
                $scope.stockMovements.push(item);
            })
        });
    };

    var setQuantityByType = function(item) {
        var quantity = item["movement.quantity"];
        switch (item["movement.type"]) {
            case 'RECEIVE' :
                item.entries = quantity;
            case 'ISSUE':
                item.issues = quantity;
            case 'NEGATIVE_ADJUST':
                item.negativeAdjustment = quantity;
            case 'POSITIVE_ADJUST':
                item.positiveAdjustment = quantity;
        }
    };

    $scope.$on('$viewContentLoaded', function () {
        $scope.productCode = $routeParams.productCode;

        $scope.loadFacilityAndStockMovements();
    });
}
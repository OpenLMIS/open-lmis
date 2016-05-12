function StockMovementReportController($scope, $routeParams, Facility, $http, CubesGenerateUrlService) {

    $scope.loadFacilityAndStockMovements = function() {
        Facility.getFacilityByCode().get({
            code: $routeParams.facilityCode
        }, function(data) {
            $scope.facilityName = data.facility.name;
            $scope.district = data.facility.geographicZone.name;
            $scope.province = data.facility.geographicZone.parent.name;

            loadStockMovements();
        });
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
            });
        });
    };

    var setQuantityByType = function(item) {
        var quantity = item["movement.quantity"];
        switch (item["movement.type"]) {
            case 'RECEIVE' :
                item.entries = quantity;
                break;
            case 'ISSUE':
                item.issues = quantity;
                break;
            case 'NEGATIVE_ADJUST':
                item.negativeAdjustment = quantity;
                break;
            case 'POSITIVE_ADJUST':
                item.positiveAdjustment = quantity;
                break;
        }
    };

    $scope.$on('$viewContentLoaded', function () {
        $scope.productCode = $routeParams.productCode;

        $scope.loadFacilityAndStockMovements();
    });
}
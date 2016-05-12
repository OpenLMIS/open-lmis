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
        cutsParams.push({dimension: "movement", values: [$scope.facilityName + "," + $scope.productCode]});

        $http.get(CubesGenerateUrlService.generateMembersUrl('vw_stock_movements', cutsParams)).success(function (data) {
            $scope.stockMovements = [];
            _.each(data.data, function (item) {
                item.date = item["movement.date"];
                setQuantityByType(item);
                $scope.stockMovements.push(item);
            });

            $scope.stockMovements = _.sortBy($scope.stockMovements, "date");
            $scope.stockMovements.reverse();
        });
    };

    var setQuantityByType = function(item) {
        var quantity = Math.abs(item["movement.quantity"]);
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
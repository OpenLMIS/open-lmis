function StockMovementReportController($scope, $routeParams, Facility, $http, CubesGenerateUrlService, DateFormatService, $cacheFactory) {

    $scope.loadFacilityAndStockMovements = function () {
        if ($cacheFactory.get('keepHistoryInStockOnHandPage') !== undefined) {
            $cacheFactory.get('keepHistoryInStockOnHandPage').put('saveDataOfStockOnHand', "yes");
        }
        if ($cacheFactory.get('keepHistoryInStockOutReportPage') !== undefined) {
            $cacheFactory.get('keepHistoryInStockOutReportPage').put('saveDataOfStockOutReportForSingleProduct', "yes");
        }

        loadStockMovements();
    };

    var loadStockMovements = function () {
        var cuts = [
            {dimension: "movement", values: [$scope.productCode]},
            {dimension: "facility", values: [$scope.facilityCode]}];

        $http.get(CubesGenerateUrlService.generateFactsUrl('vw_stock_movements', cuts)).success(function (data) {
            var firstEntry = data[0];
            $scope.facilityName = firstEntry["facility.facility_name"];
            $scope.district = firstEntry["location.district_name"];
            $scope.province = firstEntry["location.province_name"];

            $scope.stockMovements = [];
            _.each(data, function (item) {
                setQuantityByType(item);
                $scope.stockMovements.push(item);
            });

            $scope.stockMovements = _.sortBy($scope.stockMovements, function (item) {
                return [item["movement.date"], item["movement.id"]].join("_");
            });
            $scope.stockMovements.reverse();
        });
    };

    $scope.$on('messagesPopulated', function () {
        $scope.formatDate();
    });

    $scope.formatDate = function (dateString) {
        return DateFormatService.formatDateWithLocale(dateString);
    };


    var setQuantityByType = function (item) {
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
        $scope.facilityCode = $routeParams.facilityCode;

        $scope.loadFacilityAndStockMovements();
    });
}
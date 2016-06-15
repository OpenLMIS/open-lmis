function StockMovementReportController($scope, $routeParams, Facility, $http, CubesGenerateUrlService, DateFormatService, $cacheFactory) {

    $scope.loadFacilityAndStockMovements = function () {
        Facility.getFacilityByCode().get({
            code: $scope.facilityCode
        }, function (data) {
            $scope.facilityName = data.facility.name;
            $scope.district = data.facility.geographicZone.name;
            $scope.province = data.facility.geographicZone.parent.name;

            if ($cacheFactory.get('keepHistoryInStockOnHandPage') !== undefined) {
                $scope.cache = $cacheFactory.get('keepHistoryInStockOnHandPage');
                $scope.cache.put('facilityName', data.facility.name);
                $scope.cache.put('district', data.facility.geographicZone.name);
                $scope.cache.put('province', data.facility.geographicZone.parent.name);
                $scope.cache.put('saveDataOfStockOnHand', "yes");
            }
            if($cacheFactory.get('keepHistoryInStockOutReportPage') !== undefined){
                $cacheFactory.get('keepHistoryInStockOutReportPage').put('saveDataOfStockOutReportForSingleProduct', "yes");
            }

            loadStockMovements();
        });
    };

    var loadStockMovements = function () {
        var cut = {dimension: "movement", values: [[$scope.facilityCode, $scope.productCode]]};

        $http.get(CubesGenerateUrlService.generateMembersUrl('vw_stock_movements', cut)).success(function (data) {
            $scope.stockMovements = [];
            _.each(data.data, function (item) {
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
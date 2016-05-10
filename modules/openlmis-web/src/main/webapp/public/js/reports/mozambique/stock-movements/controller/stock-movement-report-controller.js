function StockMovementReportController($scope, $routeParams, StockMovementService, Facility) {

    $scope.loadFacility = function() {
        Facility.get({
            id: $routeParams.facilityId
        }, function(data) {
            $scope.facilityName = data.facility.name;
            $scope.district = data.facility.geographicZone.name;
            $scope.province = data.facility.geographicZone.parent.name;
        })
    };

    $scope.loadStockMovements = function () {
        StockMovementService.get({
            facilityId: $routeParams.facilityId,
            productCode: $routeParams.productCode
        }, function (data) {
            $scope.stockMovements = [];

            _.each(data.stockMovement, function (item) {
                setQuantityByType(item);
                sliceExtensions(item);
                $scope.stockMovements.push(item);
            })
        });

    };

    var setQuantityByType = function(item) {
        switch (item.type) {
            case 'RECEIVE' :
                item.entries = item.quantity;
            case 'ISSUE':
                item.issues = item.quantity;
            case 'NEGATIVE_ADJUST':
                item.negativeAdjustment = item.quantity;
            case 'POSITIVE_ADJUST':
                item.positiveAdjustment = item.quantity;
        }
    };

    var sliceExtensions = function(item) {
        _.each(item.extensions, function (extension) {
            if (extension.key === "soh") {
                item.soh = extension.value;
            } else if (extension.key === "signature") {
                item.signature = extension.value;
            }
        });
    };

    $scope.$on('$viewContentLoaded', function () {
        $scope.productCode = $routeParams.productCode;
        $scope.productName = $routeParams.productName;

        $scope.loadFacility();
        $scope.loadStockMovements();
    });
}
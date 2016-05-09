function StockMovementReportController($scope, $routeParams, StockMovementService) {

    $scope.loadStockMovements = function(){
        StockMovementService.get({facilityId: $routeParams.facilityId, productCode: $routeParams.productCode},function(data){
            $scope.stockMovements = data.stockMovement;
        });
    };

    $scope.$on('$viewContentLoaded', function () {
        $scope.loadStockMovements();
    });
}
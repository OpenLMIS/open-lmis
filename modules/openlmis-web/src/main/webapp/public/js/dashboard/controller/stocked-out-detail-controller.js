/**
 * Created with IntelliJ IDEA.
 * User: issa
 * Date: 3/16/14
 * Time: 9:08 PM
 * To change this template use File | Settings | File Templates.
 */

function StockedOutDetailController($scope,$routeParams,StockedOutFacilitiesByRequisitionGroup) {

    $scope.$parent.currentTab = 'DISTRICT-STOCK-OUT-DETAIL';

    $scope.$on('$viewContentLoaded', function () {
              if(!isUndefined($routeParams.programId) &&
                !isUndefined($routeParams.periodId) &&
                !isUndefined($routeParams.productId) &&
                !isUndefined($routeParams.rgroupId)){
                StockedOutFacilitiesByRequisitionGroup.get({
                    periodId: $routeParams.periodId,
                    programId: $routeParams.programId,
                    productId: $routeParams.productId,
                    rgroupId: $routeParams.rgroupId
                },function(stockData){
                    $scope.totalStockOuts = 0;
                    if(!isUndefined(stockData.stockOut)){
                        $scope.stockedOutDetails = stockData.stockOut;
                        $scope.product = _.pluck(stockData.stockOut,'product')[0];
                        $scope.location = _.pluck(stockData.stockOut,'location')[0];
                    }else{
                        $scope.resetStockedOutData();
                    }
                });
            } else{
                $scope.resetStockedOutData();
            }
    });
    $scope.resetStockedOutData = function(){
        $scope.stockedOutDetails = null;
    };
}
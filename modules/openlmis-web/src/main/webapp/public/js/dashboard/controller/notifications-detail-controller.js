/**
 * Created by issa on 4/15/14.
 */

function NotificationsDetailController($scope,$routeParams, DashboardNotificationsDetail) {

    $scope.$parent.currentTab = 'NOTIFICATIONS-DETAIL';

    $scope.$on('$viewContentLoaded', function () {
        if(!isUndefined($routeParams.category) &&
            !isUndefined($routeParams.notificationId)){
            $scope.notificationsDetail = [{facilityCode:'Code 1', facilityName:'Facility 1'},
                                          {facilityCode:'Code 2', facilityName:'Facility 2'}]
           /* DashboardNotificationsDetail.get({
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
                    $scope.resetNotificationData();
                }
            });*/
        } else{
            $scope.resetNotificationData();
        }
    });
    $scope.resetNotificationData = function(){
        $scope.notificationsDetail = null;
    };
}
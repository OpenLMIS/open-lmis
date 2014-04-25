/**
 * Created by issa on 4/15/14.
 */

function NotificationsDetailController($scope,$routeParams, DashboardNotificationsDetail, ngTableParams) {

    $scope.$parent.currentTab = 'NOTIFICATIONS-DETAIL';

    $scope.$on('$viewContentLoaded', function () {
        if(!isUndefined($routeParams.detailTable) &&
            !isUndefined($routeParams.alertId)){
            DashboardNotificationsDetail.get({alertId:$routeParams.alertId, detailTable:$routeParams.detailTable},function(stockData){
                $scope.notificationsDetail = stockData.detail;
                alert('notification detail '+JSON.stringify($scope.notificationsDetail));
                if(!isUndefined($scope.notificationsDetail)){

                    var cols =  _.keys(_.first($scope.notificationsDetail));
                    $scope.notificationColumns = [];
                    $.each(cols, function(idx,item){
                        $scope.notificationColumns.push({name:item});
                    });
                }

            });

        }

    });
    $scope.resetNotificationData = function(){
        $scope.notificationsDetail = null;
    };

    $scope.tableParams =  new ngTableParams({
        page: 1,            // show first page
        total:0,
        count: 5
    });
}
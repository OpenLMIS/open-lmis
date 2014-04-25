/**
 * Created by issa on 4/15/14.
 */

function NotificationsDetailController($scope,$routeParams,messageService, DashboardNotificationsDetail, ngTableParams) {

    $scope.$parent.currentTab = 'NOTIFICATIONS-DETAIL';

    $scope.$on('$viewContentLoaded', function () {
        if(!isUndefined($routeParams.detailTable)){
            var pageTitleKey =  'title.notification.type.'+ $routeParams.detailTable+'.detail';
            var pageTitle = messageService.get(pageTitleKey);
            $scope.detailTable = pageTitleKey === pageTitle ? '' :pageTitle;
        }

        if(!isUndefined($routeParams.detailTable) &&
            !isUndefined($routeParams.alertId)){
            DashboardNotificationsDetail.get({alertId:$routeParams.alertId, detailTable:$routeParams.detailTable},function(stockData){
                $scope.notificationsDetail = stockData.detail;
                if(!isUndefined($scope.notificationsDetail)){

                    var cols =  _.keys(_.first($scope.notificationsDetail));
                    $scope.notificationColumns = [];
                    $.each(cols, function(idx,item){
                        var colTitleKey = 'label.notification.column.'+item;
                        var colTitle = messageService.get(colTitleKey);

                        $scope.notificationColumns.push({name:item, title:colTitle === colTitleKey ? item : colTitle});
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
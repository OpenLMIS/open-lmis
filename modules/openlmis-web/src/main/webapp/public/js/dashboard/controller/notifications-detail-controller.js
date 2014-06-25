/**
 * Created by issa on 4/15/14.
 */

function NotificationsDetailController($scope,$routeParams,messageService,SettingsByKey, DashboardNotificationsDetail, ngTableParams) {

    $scope.$parent.currentTab = 'NOTIFICATIONS-DETAIL';
    $scope.notificationDetail = {};
    $scope.programId =2;
    $scope.notificationDetail.tableName = $routeParams.detailTable;
    //alert('details for '+JSON.stringify($routeParams))

    if(!isUndefined($routeParams.detailTable)){
        var pageTitleKey =  'title.notification.type.'+ $routeParams.detailTable+'.detail';
        var pageTitle = messageService.get(pageTitleKey);
        $scope.detailTable = pageTitleKey === pageTitle ? $routeParams.detailTable :pageTitle;
    }

    if(!isUndefined($routeParams.detailTable)){
        var columnsToHide =  $routeParams.detailTable+'_HIDDEN_COLUMNS';
        SettingsByKey.get({key: columnsToHide.toUpperCase()},function (data){
            if(!isUndefined(data.settings)){
                $scope.colsToHide = data.settings.value.split(",");
            }
        });

         DashboardNotificationsDetail.get({programId:$routeParams.programId, periodId:$routeParams.periodId, productId:$routeParams.productId,zoneId:$routeParams.zoneId, detailTable:$routeParams.detailTable},function(stockData){
         $scope.notificationDetail.datarows = stockData.detail;

         if(!isUndefined($scope.notificationDetail.datarows)){

         var cols =  _.keys(_.first($scope.notificationDetail.datarows));
         $scope.notificationColumns = [];
         $.each(cols, function(idx,item){
         var colTitleKey = 'label.notification.column.'+item;
         var colTitle = messageService.get(colTitleKey);
         var hideColumn = false;
         if(_.indexOf($scope.colsToHide,item) !== -1){
         hideColumn = true;
         }

         $scope.notificationColumns.push({name:item, title:colTitle === colTitleKey ? item : colTitle, hide: hideColumn});
         });

         $scope.notificationDetail.notificationColumns = $scope.notificationColumns;
         }

         });
/*        DashboardNotificationsDetail.get({alertId:$routeParams.alertId, detailTable:$routeParams.detailTable},function(stockData){
            $scope.notificationDetail.datarows = stockData.detail;

            if(!isUndefined($scope.notificationDetail.datarows)){

                var cols =  _.keys(_.first($scope.notificationDetail.datarows));
                $scope.notificationColumns = [];
                $.each(cols, function(idx,item){
                    var colTitleKey = 'label.notification.column.'+item;
                    var colTitle = messageService.get(colTitleKey);
                    var hideColumn = false;
                    if(_.indexOf($scope.colsToHide,item) !== -1){
                        hideColumn = true;
                    }

                    $scope.notificationColumns.push({name:item, title:colTitle === colTitleKey ? item : colTitle, hide: hideColumn});
                });

                $scope.notificationDetail.notificationColumns = $scope.notificationColumns;
            }

        });*/

    }

    $scope.resetNotificationData = function(){
        $scope.notificationDetail.datarows = null;
    };

    $scope.tableParams =  new ngTableParams({
        page: 1,            // show first page
        total:0,
        count: 5
    });
}
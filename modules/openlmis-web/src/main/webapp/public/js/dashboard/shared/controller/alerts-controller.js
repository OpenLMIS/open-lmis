/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

function AlertsController($scope, $filter, Alerts,StockedOutAlerts,$location, dashboardMenuService, ngTableParams) {

    var typeAlert = 'ALERT',
        typeStockOut = 'STOCKOUT',
        typeSummary = 'SUMMARY';
/*
    $scope.alertsPanel = {openAlertPanel:true, openStockPanel:true};*/
    $scope.$on('dashboardFiltering', function(event, mass) {
        $scope.getAlerts();
    });

    $scope.getAlerts = function(){

        Alerts.get({programId: $scope.formFilter.programId, periodId: $scope.formFilter.periodId, zoneId: $scope.formFilter.zoneId},function(data){

            if(!isUndefined(data.alerts)){
                $scope.alertData = _.filter(data.alerts,function(alertData){if(alertData.displaySection == typeAlert){return alertData;}});
                $scope.alertSummary = _.filter(data.alerts,function(alertData){if(alertData.displaySection == typeSummary){return alertData;}});

            }else{
                resetAlertsData();
            }
        });
        StockedOutAlerts.get({programId: $scope.formFilter.programId, periodId: $scope.formFilter.periodId, zoneId: $scope.formFilter.zoneId},function(data){
            $scope.stockOutData = data.alerts;
        });
    };

    var resetAlertsData = function(){
      $scope.alertData = $scope.stockOutData = $scope.alertSummary = null;

    };

    var initTableParams = function(){
        return new ngTableParams({
            page: 1,            // show first page
            total:0,
            count: 5
        });
    };

    $scope.showAlertDetails = function(detailTable){
        var notificationPath = 'notifications/'+$scope.formFilter.programId+'/'+$scope.formFilter.periodId+'/'+$scope.formFilter.zoneId+'/'+detailTable;
        dashboardMenuService.addTab('menu.header.dashboard.notifications.detail','/public/pages/dashboard/index.html#/'+notificationPath,'NOTIFICATIONS-DETAIL',true, 7);

        //$location.path(notificationPath).search("programId="+$scope.formFilter.programId);
        $location.path(notificationPath);
    };

    $scope.showStockedOutAlertDetails = function(detailTable,productId){
        var notificationPath = 'notifications/'+$scope.formFilter.programId+'/'+$scope.formFilter.periodId+'/'+$scope.formFilter.zoneId+'/'+productId+'/'+detailTable;
        dashboardMenuService.addTab('menu.header.dashboard.notifications.detail','/public/pages/dashboard/index.html#/'+notificationPath,'NOTIFICATIONS-DETAIL',true, 7);

        $location.path(notificationPath);
    };



    $scope.alertTableParams = initTableParams();
    $scope.stockOutsTableParams = initTableParams();

    $scope.dataRows = {};

    $scope.tableParamsChanged = function(params, rawData, category) {
        if(isUndefined(rawData)){
            if(category === typeAlert){
                $scope.dataRows.alert = [];
            }else if(category === typeStockOut){
                $scope.dataRows.stock = [];
            }
            params.total = 0;
        }else {
            var data = rawData;
            var orderedData = params.filter ? $filter('filter')(data, params.filter) : data;
            orderedData = params.sorting ?  $filter('orderBy')(orderedData, params.orderBy()) : data;

            params.total = orderedData.length;
            if(category === typeAlert){
                $scope.dataRows.alert = orderedData.slice( (params.page - 1) * params.count,  params.page * params.count );
            }else if(category === typeStockOut){
                $scope.dataRows.stock = orderedData.slice( (params.page - 1) * params.count,  params.page * params.count );
            }
        }

    };

    $scope.$watch('alertData',function(){

        $scope.alertTableParams = initTableParams();
        $scope.tableParamsChanged($scope.alertTableParams, $scope.alertData, typeAlert);
    });
    $scope.$watch('alertTableParams',function(newParams){
        $scope.alertTableParams = newParams;
        $scope.tableParamsChanged($scope.alertTableParams, $scope.alertData, typeAlert);
    });


    $scope.$watch('stockOutsTableParams',function(newParams){

        $scope.stockOutsTableParams = newParams;
        $scope.tableParamsChanged($scope.stockOutsTableParams, $scope.stockOutData, typeStockOut);
    });

    $scope.$watch('stockOutData', function(){
        $scope.stockOutsTableParams = initTableParams();
        $scope.tableParamsChanged($scope.stockOutsTableParams, $scope.stockOutData, typeStockOut);
    });
}

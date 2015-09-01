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

function StockController($scope,userPreferredFilters,$timeout,$routeParams,dashboardFiltersHistoryService,programsList,FlatGeographicZoneList,UserGeographicZoneTree, formInputValue,GetPeriod, ReportSchedules, ReportPeriods, ReportProductsByProgram, OperationYears, ReportPeriodsByScheduleAndYear, StockEfficiencyDetail, ngTableParams) {


    initialize();

    function initialize() {
        $scope.stockStatusMapping = [{name: "Stocked out",key:"S", value:"0"},
            {name: "Understocked",key:"U", value:"1"},
            {name: "Overstocked",key:"O", value:"2"},
            {name: "Adequately Stocked",key:"A", value:"3"}];

        $scope.$parent.currentTab = 'STOCK';
        $scope.showProductsFilter = true;
        $scope.productSelectOption = {maximumSelectionSize : 4};
        $scope.showStockStatusFilter = true;
        $scope.stockStatusMapping.unshift({'name':'-- All --','value':-1});
    }

    var filterHistory = dashboardFiltersHistoryService.get($scope.$parent.currentTab);
    if(_.isEmpty($routeParams) && isUndefined(filterHistory)){
        $scope.formFilter = $scope.filterObject  = userPreferredFilters || {};

    }else if(!_.isEmpty($routeParams)){
        $scope.formFilter = $scope.filterObject  = $routeParams;
        $scope.formFilter.productIdList = $scope.filterObject.productIdList = [$routeParams.productId];
    }else{
        $scope.formFilter = $scope.filterObject  = filterHistory || {};
    }


    $scope.formPanel = {openPanel:true};

    $scope.alertsPanel = {openAlertPanel:true, openStockPanel:true};


    FlatGeographicZoneList.get(function (data) {
        $scope.geographicZones = data.zones;
    });

    $scope.programs = programsList;
    $scope.programs.unshift({'name': formInputValue.programOptionSelect});

    $scope.loadGeoZones = function(){
        UserGeographicZoneTree.get({programId:$scope.formFilter.programId}, function(data){
            $scope.zones = data.zone;
            if(!isUndefined($scope.zones)){
                $scope.rootZone = $scope.zones.id;
            }
        });
    };

    OperationYears.get(function (data) {
        $scope.startYears = data.years;
        $scope.startYears.unshift(formInputValue.yearOptionAll);
    });

    ReportSchedules.get(function(data){
        $scope.schedules = data.schedules;
        $scope.schedules.unshift({'name': formInputValue.scheduleOptionSelect}) ;
    });

    $scope.filterProductsByProgram = function (){
        $scope.loadGeoZones();

        if(isUndefined($scope.formFilter.programId)){
            $scope.products = null;
            $scope.resetStockingData();
            return;
        }
        $scope.filterObject.programId = $scope.formFilter.programId;
        ReportProductsByProgram.get({programId:  $scope.filterObject.programId}, function(data){
            $scope.products = data.productList;
        });

    };

    $scope.processProductsFilter = function (){
        $scope.filterObject.productIdList = $scope.formFilter.productIdList;
    };

    $scope.changeSchedule = function(){

        if (!isUndefined($scope.formFilter.scheduleId)) {
            $scope.filterObject.scheduleId = $scope.formFilter.scheduleId;
        }

        if(!isUndefined($scope.filterObject.scheduleId) ){
            if(!isUndefined($scope.filterObject.year) ){
                ReportPeriodsByScheduleAndYear.get({scheduleId: $scope.filterObject.scheduleId, year: $scope.filterObject.year}, function(data){
                    $scope.periods = data.periods;
                    $scope.periods.unshift({'name':formInputValue.periodOptionSelect});
                });
            }else{
                ReportPeriods.get({ scheduleId : $scope.filterObject.scheduleId },function(data) {
                    $scope.periods = data.periods;
                    $scope.periods.unshift({'name': formInputValue.periodOptionSelect});
                });
            }
        }
    };

    $scope.changeScheduleByYear = function (){

        if (!isUndefined($scope.formFilter.year)) {
            $scope.filterObject.year = $scope.formFilter.year;
        }
        $scope.changeSchedule();

    };

    $scope.processPeriodFilter = function (){
        if (!isUndefined($scope.formFilter.periodId)) {
            $scope.filterObject.periodId = $scope.formFilter.periodId;
        }
    };

    $scope.processStockStatusFilter = function(){
        if(!isUndefined($scope.formFilter.status)) {
        }else{
            $scope.formFilter.status = -1;
        }
    };
    // the grid options
    $scope.tableParams = new ngTableParams({
        page: 1,            // show first page
        total: 0,           // length of data
        count: 25           // count per page
    });

    $scope.loadStockingData = function(){
        if(!isUndefined($scope.filterObject.productIdList)){
            StockEfficiencyDetail.get({
                periodId: $scope.filterObject.periodId,
                programId: $scope.filterObject.programId,
                zoneId: $scope.filterObject.zoneId,
                productListId: $scope.filterObject.productIdList
            },function (data){
                $scope.stockingList = data.stocking;

                if($scope.formFilter.status && $scope.formFilter.status !== -1){

                    var statusMap = _.findWhere($scope.stockStatusMapping,{value:$scope.formFilter.status});
                    if(statusMap){
                        $scope.stockingList = _.where( $scope.stockingList, {stocking:statusMap.key});
                    }
                }
                $scope.stockByProductAndStock = groupStockingByProductAndStock($scope.stockingList);

            });
        }else{
            $scope.stockByProductAndStock = undefined;
        }

    };

    $scope.resetStockingData = function(){
        $scope.stockingList = $scope.stockByProductAndStock = null;
    };

    var groupStockingByProductAndStock = function (data) {
        if(isUndefined(data) || _.isNull(data)){
            return data;
        }
        var groupedByProductAndStocking = [];
        var groupedByProduct = _.chain(data).groupBy('productId').map(function(value, key) { return {productId: key, product: _.first(value).product, stocks: value, openPanel:true };}).value();
        $scope.stockTableParams = {};
        angular.forEach(groupedByProduct, function(productGroup){

            var groupedByStocking = _.chain(productGroup.stocks).
                                      groupBy('stocking').
                                      map(function(value, key) {
                                        var statusMap = _.findWhere($scope.stockStatusMapping,{key:key});
                                        var stockStatusDesc = !isUndefined(statusMap) ? statusMap.name : key;

                                        return {stocking: key, name: stockStatusDesc, facilities: value };

                                      }).value();

            var statuses = _.reject($scope.stockStatusMapping,function(status){return status.value === -1;});
            var existingStat = _.pluck(groupedByStocking,'stocking');
            var statusMapping = _.pluck(statuses,'key');
            var nonexistingStat = _.difference(statusMapping,existingStat);
            angular.forEach(nonexistingStat, function(status){
                var statusMap = _.findWhere($scope.stockStatusMapping,{key:status});
                var stockStatusDesc = !isUndefined(statusMap) ? statusMap.name : key;
                groupedByStocking.push({stocking: status, name: stockStatusDesc, facilities: null});
            });
            groupedByProductAndStocking.push({productId: productGroup.productId, product: productGroup.product, stocks:groupedByStocking });
        });
        return groupedByProductAndStocking;
    };

    $scope.processZoneFilter = function(){
        $scope.filterObject.zoneId = $scope.formFilter.zoneId;
    };
    $scope.$on('$viewContentLoaded', function () {
        $timeout(function(){
            $scope.search();
        },1000);
    });

    var getFilterValues = function () {
        $scope.formFilter.programName = getSelectedItemName($scope.formFilter.programId, $scope.programs);
        $scope.formFilter.periodName = getSelectedItemName($scope.formFilter.periodId,$scope.periods);
        $scope.formFilter.zoneName = getSelectedZoneName($scope.formFilter.zoneId, $scope.zones, $scope.geographicZones);
    };
    $scope.search = function(){
        getFilterValues();
        if($scope.rootZone == $scope.formFilter.zoneId){
            return;
        }
        $scope.loadStockingData();
        //Alert Controller listens this event to update its own data
        $scope.$broadcast('dashboardFiltering', null);
    };

    $scope.$watch('formFilter.programId',function(){
        $scope.filterProductsByProgram();

    });
    $scope.$watch('formFilter.scheduleId', function(){
        $scope.changeSchedule();
    });

    $scope.$on('$routeChangeStart', function(){
        var data = {};
        angular.extend(data,$scope.filterObject);
        dashboardFiltersHistoryService.add($scope.$parent.currentTab, data);
    });


}
